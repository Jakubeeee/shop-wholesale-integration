package com.jakubeeee.tasks.publishers;

import com.jakubeeee.tasks.model.LogMessage;
import com.jakubeeee.tasks.model.PastTaskExecution;
import com.jakubeeee.tasks.model.ProgressTracker;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jakubeeee.common.model.MessageWrapper.Action.ADD_TO_STATE;
import static com.jakubeeee.common.model.MessageWrapper.Action.SWAP_STATE;
import static com.jakubeeee.common.util.JsonUtils.objectToJson;
import static com.jakubeeee.common.util.JsonUtils.wrap;

@Component
public class TaskPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public TaskPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishSingleNextScheduledTaskExecution(Map<Long, String> nextScheduledTaskExecution) {
        Optional<String> nextScheduledTaskExecutionO = objectToJson(wrap(nextScheduledTaskExecution, ADD_TO_STATE));
        nextScheduledTaskExecutionO.ifPresent(nextScheduledTaskExecutionAsJson ->
                messagingTemplate.convertAndSend("/topic/nextScheduledTasksExecutions",
                        nextScheduledTaskExecutionAsJson));
    }

    public void publishAllTasksLogs(List<LogMessage> logList) {
        Optional<String> logListAsJsonO = objectToJson(wrap(logList, SWAP_STATE));
        logListAsJsonO.ifPresent(logListAsJson ->
                messagingTemplate.convertAndSend("/topic/tasksLogs", logListAsJson));
    }

    public void publishPartOfTasksLogs(List<LogMessage> logList) {
        Optional<String> logAsJsonO = objectToJson(wrap(logList, ADD_TO_STATE));
        logAsJsonO.ifPresent(logAsJson ->
                messagingTemplate.convertAndSend("/topic/tasksLogs", logAsJson));
    }

    public void publishTasksProgress(Map<Long, ProgressTracker> progressTrackers) {
        Optional<String> progressTrackersAsJsonO = objectToJson(wrap(progressTrackers, SWAP_STATE));
        progressTrackersAsJsonO.ifPresent(progressTrackersAsJson ->
                messagingTemplate.convertAndSend("/topic/tasksProgress", progressTrackersAsJson));
    }

    public void publishPastTaskExecutions(List<PastTaskExecution> pastTaskExecutions) {
        Optional<String> pastTaskExecutionsAsJsonO = objectToJson(wrap(pastTaskExecutions, SWAP_STATE));
        pastTaskExecutionsAsJsonO.ifPresent(pastTaskExecutionsAsJson ->
                messagingTemplate.convertAndSend("/topic/pastTasksExecutions", pastTaskExecutionsAsJson));
    }

}
