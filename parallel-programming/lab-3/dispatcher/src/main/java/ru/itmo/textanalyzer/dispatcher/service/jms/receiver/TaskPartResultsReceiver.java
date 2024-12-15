package ru.itmo.textanalyzer.dispatcher.service.jms.receiver;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;
import ru.itmo.textanalyzer.commons.dto.TaskPartResultDto;
import ru.itmo.textanalyzer.dispatcher.service.TaskPartService;
import ru.itmo.textanalyzer.dispatcher.service.TaskService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPartResultsReceiver {

    private final TaskService taskService;
    private final TaskPartService taskPartService;

    @JmsListener(destination = QueuesNames.TASK_PART_RESULTS_QUEUE, concurrency = "1-32")
    public void receiveTaskPartResultMessage(Message message) throws JMSException {
        TaskPartResultDto result = message.getBody(TaskPartResultDto.class);
        log.info("Received result from task part with ID: {}", result.getTaskPartId());
        taskPartService.addTaskPartResult(result);
        int taskId = taskPartService.getTaskIdByTaskPartId(result.getTaskPartId());
        taskService.tryCalculateResult(taskId);
    }
}
