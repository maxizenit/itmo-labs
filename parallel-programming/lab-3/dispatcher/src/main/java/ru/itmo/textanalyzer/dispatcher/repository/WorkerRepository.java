package ru.itmo.textanalyzer.dispatcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.itmo.textanalyzer.dispatcher.model.entity.Worker;

import java.util.Set;

public interface WorkerRepository extends CrudRepository<Worker, Long> {

    Set<Worker> getWorkersByEnabled(Boolean enabled);
}
