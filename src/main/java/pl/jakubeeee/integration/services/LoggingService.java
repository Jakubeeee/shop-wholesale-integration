package pl.jakubeeee.integration.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.jakubeeee.integration.domain.LogMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static java.time.LocalDateTime.now;
import static pl.jakubeeee.integration.domain.LogMessage.Type.*;
import static pl.jakubeeee.integration.utils.DateUtils.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class LoggingService {

    final int LOG_REMOVAL_HOURS_THRESHOLD = 3;

    @Getter
    List<LogMessage> logList = new CopyOnWriteArrayList<>();

    public void error(String message) {
        addMessage(message, ERROR);
        LOG.error(message);
    }

    public void warn(String message) {
        addMessage(message, WARN);
        LOG.warn(message);
    }

    public void update(String message) {
        addMessage(message, UPDATE);
        LOG.info(message);
    }

    public void info(String message) {
        addMessage(message, INFO);
        LOG.info(message);
    }

    public void debug(String message) {
        addMessage(message, DEBUG);
        LOG.debug(message);
    }

    private void addMessage(String message, LogMessage.Type type) {
        logList.add(new LogMessage(message, type, format(now())));
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
