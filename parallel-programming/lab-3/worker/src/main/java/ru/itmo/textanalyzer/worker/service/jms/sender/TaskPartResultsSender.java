package ru.itmo.textanalyzer.worker.service.jms.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;
import ru.itmo.textanalyzer.commons.dto.TaskPartResultDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPartResultsSender {

    private final JmsTemplate jmsTemplate;

    public void sendTaskPartResult(TaskPartResultDto result) {
        jmsTemplate.convertAndSend(QueuesNames.TASK_PART_RESULTS_QUEUE, result);
    }
}
