package ru.itmo.textanalyzer.worker.service.jms.receiver;

import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;
import ru.itmo.textanalyzer.worker.service.WorkerIdProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewWorkerIdsReceiver {

    private final JmsTemplate jmsTemplate;
    private final WorkerIdProvider workerIdProvider;
    private final WorkerTaskPartsReceiver workerTaskPartsReceiver;

    @SuppressWarnings("all")
    public void receiveNewWorkersIdsMessage() throws JMSException {
        int workerId = jmsTemplate.receive(QueuesNames.NEW_WORKER_IDS_QUEUE).getBody(Integer.class);
        log.info("Received new worker ID message");
        workerIdProvider.setWorkerId(workerId);
        workerTaskPartsReceiver.start();
    }
}
