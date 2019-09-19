package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.model.LogMessage;
import com.jakubeeee.tasks.model.LogParam;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Interface for service beans used for operations related to task logging.
 */
public interface LoggingService {

    void error(long taskId, String code);

    void error(long taskId, String code, @Nullable List<LogParam> params);

    void error(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts);

    void warn(long taskId, String code);

    void warn(long taskId, String code, @Nullable List<LogParam> params);

    void warn(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts);

    void update(long taskId, String code);

    void update(long taskId, String code, @Nullable List<LogParam> params);

    void update(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts);

    void info(long taskId, String code);

    void info(long taskId, String code, @Nullable List<LogParam> params);

    void info(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts);

    void debug(long taskId, String code);

    void debug(long taskId, String code, @Nullable List<LogParam> params);

    void debug(long taskId, String code, @Nullable List<LogParam> params, int dynamicParts);

    void startPublishingLogs();

    List<LogMessage> getAllCachedLogs();

    void clearLogList();

    void removeUnnecessaryLogs();

}
