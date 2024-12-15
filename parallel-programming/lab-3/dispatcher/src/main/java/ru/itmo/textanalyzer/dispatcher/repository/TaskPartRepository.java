package ru.itmo.textanalyzer.dispatcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.itmo.textanalyzer.dispatcher.model.entity.Task;
import ru.itmo.textanalyzer.dispatcher.model.entity.TaskPart;

import java.util.List;

public interface TaskPartRepository extends CrudRepository<TaskPart, Integer> {

    List<TaskPart> getTaskPartByTask(Task task);

    void removeAllByTask(Task task);
}
