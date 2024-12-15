package ru.itmo.textanalyzer.dispatcher.service.jms.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;
import ru.itmo.textanalyzer.commons.dto.TaskPartDto;
import ru.itmo.textanalyzer.dispatcher.model.entity.TaskPart;
import ru.itmo.textanalyzer.dispatcher.model.mapper.TaskPartMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPartsSender {

    private final JmsTemplate jmsTemplate;
    private final TaskPartMapper taskPartMapper;

    public void sendTaskPart(TaskPart taskPart) {
        int workerId = taskPart.getWorker().getId();
        log.info("Sending new worker task part with ID: {} to worker with ID {}", taskPart.getId(), workerId);
        TaskPartDto taskPartDto = taskPartMapper.fromTaskPartToTaskPartDto(taskPart);
        jmsTemplate.convertAndSend(QueuesNames.WORKER_TASK_PARTS_QUEUE.formatted(workerId), taskPartDto);
    }
}
