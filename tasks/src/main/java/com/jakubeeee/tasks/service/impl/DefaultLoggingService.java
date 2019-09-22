package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.core.service.TimerService;
import com.jakubeeee.tasks.model.LogMessage;
import com.jakubeeee.tasks.model.LogParam;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import com.jakubeeee.tasks.service.LoggingService;
import com.jakubeeee.tasks.service.ProgressTrackingService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.jakubeeee.common.CollectionUtils.extractList;
import static com.jakubeeee.common.CollectionUtils.filterList;
import static com.jakubeeee.common.DateTimeUtils.*;
import static com.jakubeeee.common.LangUtils.nvl;
import static com.jakubeeee.tasks.model.LogMessage.Type.*;

/**
 * Default service bean used for operations related to task logging.
 */
@RequiredArgsConstructor
@Service
public class DefaultLoggingService implements LoggingService {

    private static final int LOG_REMOVAL_HOURS_THRESHOLD = 5;
    private static final int LOG_LIST_MAX_SIZE = 4000;
    private static final int LOGS_PUBLISHING_INTERVAL = 1000;

    private final ProgressTrackingService progressTrackingService;

    private final TaskPublisher taskPublisher;

    private final TimerService timerService;

    @Getter
    private List<LogMessage> allCachedLogs = new ArrayList<>();

    private List<LogMessage> temporaryPendingLogs = new ArrayList<>();

    private boolean isPublishing = false;

    @Override
    public void error(long taskId, String code) {
        error(taskId, code, null, 0);
    }

    @Override
    public void error(long taskId, String code, @Nullable List<LogParam> params) {
        error(taskId, code, params, 0);
    }

    @Override
    public void error(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, ERROR, params, dynamicParts);
    }

    @Override
    public void warn(long taskId, String code) {
        warn(taskId, code, null, 0);
    }

    @Override
    public void warn(long taskId, String code, @Nullable List<LogParam> params) {
        warn(taskId, code, params, 0);
    }

    @Override
    public void warn(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, WARN, params, dynamicParts);
    }

    @Override
    public void update(long taskId, String code) {
        update(taskId, code, null, 0);
    }

    @Override
    public void update(long taskId, String code, @Nullable List<LogParam> params) {
        update(taskId, code, params, 0);
    }

    @Override
    public void update(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, UPDATE, params, dynamicParts);
    }

    @Override
    public void info(long taskId, String code) {
        info(taskId, code, null, 0);
    }

    @Override
    public void info(long taskId, String code, @Nullable List<LogParam> params) {
        info(taskId, code, params, 0);
    }

    @Override
    public void info(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, INFO, params, dynamicParts);
    }

    @Override
    public void debug(long taskId, String code) {
        debug(taskId, code, null, 0);
    }

    @Override
    public void debug(long taskId, String code, @Nullable List<LogParam> params) {
        debug(taskId, code, params, 0);
    }

    @Override
    public void debug(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts) {
        createLogMessage(taskId, code, DEBUG, params, dynamicParts);
    }

    private void createLogMessage(long taskId, String code, LogMessage.Type type, @Nullable List<LogParam> params,
                                  int dynamicParts) {
        var log = new LogMessage(taskId, code, type, formatDateTimeWithNanos(getCurrentDateTime()), nvl(params,
                new ArrayList<>()), dynamicParts);
        cacheLogMessage(log);
    }

    @Synchronized
    private void cacheLogMessage(LogMessage log) {
        allCachedLogs.add(log);
        temporaryPendingLogs.add(log);
    }

    @Override
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
    @Override
    public void clearLogList() {
        allCachedLogs.clear();
        temporaryPendingLogs.clear();
    }

    @Synchronized
    @Scheduled(fixedRate = 1200000)
    @Override
    public void removeUnnecessaryLogs() {
        LocalDateTime timeHoursAgo = getCurrentDateTime().minusHours(LOG_REMOVAL_HOURS_THRESHOLD);
        if (allCachedLogs.size() > LOG_LIST_MAX_SIZE) {
            int excess = allCachedLogs.size() - LOG_LIST_MAX_SIZE;
            allCachedLogs = allCachedLogs.subList(excess, allCachedLogs.size());
        }
        allCachedLogs = filterList(allCachedLogs, (log -> !isTimeAfter(timeHoursAgo,
                parseStringToDateTime(log.getTime()))));
        taskPublisher.publishAllTasksLogs(allCachedLogs);
    }

}
