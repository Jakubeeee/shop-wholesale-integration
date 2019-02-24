package com.jakubeeee.tasks.service;

import com.jakubeeee.core.service.TimerService;
import com.jakubeeee.tasks.model.LogMessage;
import com.jakubeeee.tasks.model.LogParam;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
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
        error(taskId, code, null, 0);
    }

    public void error(long taskId, String code, @Nullable List<LogParam> params) {
        error(taskId, code, params, 0);
    }

    public void error(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, ERROR, params, dynamicParts);
    }

    public void warn(long taskId, String code) {
        warn(taskId, code, null, 0);
    }

    public void warn(long taskId, String code, @Nullable List<LogParam> params) {
        warn(taskId, code, params, 0);
    }

    public void warn(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, WARN, params, dynamicParts);
    }

    public void update(long taskId, String code) {
        update(taskId, code, null, 0);
    }

    public void update(long taskId, String code, @Nullable List<LogParam> params) {
        update(taskId, code, params, 0);
    }

    public void update(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, UPDATE, params, dynamicParts);
    }

    public void info(long taskId, String code) {
        info(taskId, code, null, 0);
    }

    public void info(long taskId, String code, @Nullable List<LogParam> params) {
        info(taskId, code, params, 0);
    }

    public void info(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, INFO, params, dynamicParts);
    }

    public void debug(long taskId, String code) {
        debug(taskId, code, null, 0);
    }

    public void debug(long taskId, String code, @Nullable List<LogParam> params) {
        debug(taskId, code, params, 0);
    }

    public void debug(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, DEBUG, params, dynamicParts);
    }

    private void createLogMessage(long taskId, String code, LogMessage.Type type, @Nullable List<LogParam> params, int dynamicParts) {
        var log = new LogMessage(taskId, code, type, formatDateTimeWithNanos(now()), nvl(params, new ArrayList<>()), dynamicParts);
        cacheLogMessage(log);
    }

    @Synchronized
    private void cacheLogMessage(LogMessage log) {
        allCachedLogs.add(log);
        temporaryPendingLogs.add(log);
    }

    void startPublishingLogs() {
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
