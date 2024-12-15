package ru.itmo.textanalyzer.dispatcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.itmo.textanalyzer.dispatcher.model.enm.TaskStatus;
import ru.itmo.textanalyzer.dispatcher.model.entity.Task;

import java.util.Set;

public interface TaskRepository extends CrudRepository<Task, Integer> {

    Set<Task> findTasksByStatus(TaskStatus status);
}
