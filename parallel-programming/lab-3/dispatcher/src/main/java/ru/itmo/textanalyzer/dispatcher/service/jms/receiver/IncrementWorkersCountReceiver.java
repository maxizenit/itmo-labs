package ru.itmo.textanalyzer.dispatcher.service.jms.receiver;

import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.QueuesNames;
import ru.itmo.textanalyzer.dispatcher.service.WorkerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncrementWorkersCountReceiver {

    private final WorkerService workerService;

    @JmsListener(destination = QueuesNames.INCREMENT_WORKERS_COUNT_QUEUE)
    @SuppressWarnings("unused")
    public void receiveIncrementWorkersCount(Message message) {
        log.info("Received increment workers count message");
        workerService.createNewWorker();
    }
}
