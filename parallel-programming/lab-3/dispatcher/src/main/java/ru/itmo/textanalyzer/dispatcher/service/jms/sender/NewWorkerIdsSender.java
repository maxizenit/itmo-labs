package ru.itmo.textanalyzer.dispatcher.service.jms.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewWorkerIdsSender {

    private final JmsTemplate jmsTemplate;

    public void sendNewWorkerId(int newWorkerId) {
        log.info("Sending new worker ID: {}", newWorkerId);
        jmsTemplate.convertAndSend(QueuesNames.NEW_WORKER_IDS_QUEUE, newWorkerId);
    }
}
