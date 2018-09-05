package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.LogMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static com.jakubeeee.common.utils.DateTimeUtils.*;
import static com.jakubeeee.common.utils.LangUtils.nvl;
import static com.jakubeeee.integration.model.LogMessage.Type.*;
import static java.time.LocalDateTime.now;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class LoggingService {

    final int LOG_REMOVAL_HOURS_THRESHOLD = 3;

    @Getter
    List<LogMessage> logList = new CopyOnWriteArrayList<>();

    public void error(String code) {
        error(code, null);
    }

    public void error(String code, List<String> params) {
        addMessage(code, ERROR, params);
        LOG.error(code);
    }

    public void warn(String code) {
        warn(code, null);
    }

    public void warn(String code, List<String> params) {
        addMessage(code, WARN, params);
        LOG.warn(code);
    }

    public void update(String code) {
        update(code, null);
    }

    public void update(String code, List<String> params) {
        addMessage(code, UPDATE, params);
        LOG.info(code);
    }

    public void info(String code) {
        info(code, null);
    }

    public void info(String code, List<String> params) {
        addMessage(code, INFO, params);
        LOG.info(code);
    }

    public void debug(String code) {
        debug(code, null);
    }

    public void debug(String code, List<String> params) {
        addMessage(code, DEBUG, params);
        LOG.debug(code);
    }

    private void addMessage(String code, LogMessage.Type type, @Nullable List<String> params) {
        logList.add(new LogMessage(code, type, format(now()), nvl(params, new ArrayList<>())));
    }

    public void clearLogList() {
        logList.clear();
    }

    public void removeOldLogs() {
        LocalDateTime timeHoursAgo = now().minusHours(LOG_REMOVAL_HOURS_THRESHOLD);
        Predicate<LogMessage> predicate = c -> isAfter(timeHoursAgo, parse(c.getTime()));
        logList.stream().filter(predicate).forEach(c -> logList.remove(c));
    }

}
