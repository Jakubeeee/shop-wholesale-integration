package com.jakubeeee.tasks.service;

import com.jakubeeee.common.service.TimerService;
import com.jakubeeee.tasks.model.LogMessage;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.jakubeeee.common.utils.DateTimeUtils.*;
import static com.jakubeeee.common.utils.LangUtils.*;
import static com.jakubeeee.tasks.model.LogMessage.Type.*;
import static java.time.LocalDateTime.now;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class LoggingService {

    final int LOG_REMOVAL_HOURS_THRESHOLD = 5;
    final int LOG_LIST_MAX_SIZE = 4000;
    final int LOGS_PUBLISHING_INTERVAL = 1000;

    @Getter
    List<LogMessage> allCachedLogs = new ArrayList<>();

    List<LogMessage> temporaryPendingLogs = new ArrayList<>();

    boolean isPublishing = false;

    @Autowired
    ProgressTrackingService progressTrackingService;

    @Autowired
    TaskPublisher taskPublisher;

    @Autowired
    TimerService timerService;

    public void error(long taskId, String code) {
        error(taskId, code, null);
    }

    public void error(long taskId, String code, @Nullable List<String> params) {
        createLogMessage(taskId, code, ERROR, params);
    }

    public void warn(long taskId, String code) {
        warn(taskId, code, null);
    }

    public void warn(long taskId, String code, @Nullable List<String> params) {
        createLogMessage(taskId, code, WARN, params);
    }

    public void update(long taskId, String code) {
        update(taskId, code, null);
    }

    public void update(long taskId, String code, @Nullable List<String> params) {
        createLogMessage(taskId, code, UPDATE, params);
    }

    public void info(long taskId, String code) {
        info(taskId, code, null);
    }

    public void info(long taskId, String code, @Nullable List<String> params) {
        createLogMessage(taskId, code, INFO, params);
    }

    public void debug(long taskId, String code) {
        debug(taskId, code, null);
    }

    public void debug(long taskId, String code, @Nullable List<String> params) {
        createLogMessage(taskId, code, DEBUG, params);
    }

    private void createLogMessage(long taskId, String code, LogMessage.Type type, @Nullable List<String> params) {
        var log = new LogMessage(taskId, code, type, formatDateTimeWithNanos(now()), nvl(params, new ArrayList<>()));
        cacheLogMessage(log);
    }

    @Synchronized
    private void cacheLogMessage(LogMessage log) {
        allCachedLogs.add(log);
        temporaryPendingLogs.add(log);
    }

    public void startPublishingLogs() {
        if (isPublishing) return;
        isPublishing = true;
        BooleanSupplier cancelCondition = () -> !progressTrackingService.isAnyProgressTrackerActive();
        timerService.setRecurrentJob(() -> taskPublisher.publishPartOfTasksLogs(extractTemporaryPendingLogs()), 0,
                LOGS_PUBLISHING_INTERVAL, cancelCondition, () -> isPublishing = false, false);
    }

    @Synchronized
    private List<LogMessage> extractTemporaryPendingLogs() {
        return extractList(temporaryPendingLogs);
    }

    @Synchronized
    public void clearLogList() {
        allCachedLogs.clear();
        temporaryPendingLogs.clear();
    }

    @Synchronized
    @Scheduled(fixedRate = 1200000)
    public void removeUnnecessaryLogs() {
        LocalDateTime timeHoursAgo = now().minusHours(LOG_REMOVAL_HOURS_THRESHOLD);
        if (allCachedLogs.size() > LOG_LIST_MAX_SIZE) {
            int excess = allCachedLogs.size() - LOG_LIST_MAX_SIZE;
            allCachedLogs = allCachedLogs.subList(excess, allCachedLogs.size());
        }
        allCachedLogs = filterList(allCachedLogs, (log -> !isTimeAfter(timeHoursAgo, stringToTime(log.getTime()))));
        taskPublisher.publishAllTasksLogs(allCachedLogs);
    }

}
