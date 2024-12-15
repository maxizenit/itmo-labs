package ru.itmo.textanalyzer.dispatcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.dispatcher.model.entity.Worker;
import ru.itmo.textanalyzer.dispatcher.repository.WorkerRepository;
import ru.itmo.textanalyzer.dispatcher.service.jms.sender.NewWorkerIdsSender;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final NewWorkerIdsSender newWorkerIdsSender;

    public void createNewWorker() {
        Worker worker = new Worker();
        worker.setEnabled(true);
        workerRepository.save(worker);

        int workerId = worker.getId();
        log.info("Saved new worker with ID: {}", workerId);
        newWorkerIdsSender.sendNewWorkerId(workerId);
    }

    public Set<Worker> getAvailableWorkers() {
        return workerRepository.getWorkersByEnabled(true);
    }
}
