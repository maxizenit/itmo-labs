package ru.itmo.textanalyzer.worker.service.jms.sender;

import jakarta.annotation.PostConstruct;
import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;
import ru.itmo.textanalyzer.worker.service.jms.receiver.NewWorkerIdsReceiver;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncrementWorkersCountSender {

    private final JmsTemplate jmsTemplate;
    private final NewWorkerIdsReceiver newWorkerIdsReceiver;

    @PostConstruct
    public void init() throws JMSException {
        sendIncrementWorkersCount();
    }

    public void sendIncrementWorkersCount() throws JMSException {
        log.info("Sending increment workers count message");
        jmsTemplate.convertAndSend(QueuesNames.INCREMENT_WORKERS_COUNT_QUEUE, "");
        newWorkerIdsReceiver.receiveNewWorkersIdsMessage();
    }
}
