package ru.itmo.textanalyzer.worker.service.jms.receiver;

import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;
import ru.itmo.textanalyzer.commons.dto.TaskPartDto;
import ru.itmo.textanalyzer.worker.service.TaskPartService;
import ru.itmo.textanalyzer.worker.service.WorkerIdProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerTaskPartsReceiver {

    private final JmsTemplate jmsTemplate;
    private final WorkerIdProvider workerIdProvider;
    private final TaskPartService taskPartService;

    private boolean isEnabled = false;

    public void start() throws JMSException {
        isEnabled = true;
        receiveWorkerTaskParts();
    }

    @SuppressWarnings("all")
    public void receiveWorkerTaskParts() throws JMSException {
        while (isEnabled) {
            TaskPartDto taskPart =
                    jmsTemplate.receive(QueuesNames.WORKER_TASK_PARTS_QUEUE.formatted(workerIdProvider.getWorkerId()))
                            .getBody(TaskPartDto.class);
            log.info("Received worker task part with ID: {}", taskPart.getId());
            taskPartService.executeTaskPart(taskPart);
        }
    }
}
