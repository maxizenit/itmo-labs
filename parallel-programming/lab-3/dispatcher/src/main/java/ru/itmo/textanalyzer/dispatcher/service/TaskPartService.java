package ru.itmo.textanalyzer.dispatcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.dto.TaskPartResultDto;
import ru.itmo.textanalyzer.dispatcher.model.entity.Task;
import ru.itmo.textanalyzer.dispatcher.model.entity.TaskPart;
import ru.itmo.textanalyzer.dispatcher.model.entity.Worker;
import ru.itmo.textanalyzer.dispatcher.repository.TaskPartRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPartService {

    private static final Set<Character> END_SENTENCE_CHARS = Set.of('.', '!', '?');

    private final TaskPartRepository taskPartRepository;
    private final WorkerService workerService;

    public Set<TaskPart> createTaskParts(Task task) {
        Set<TaskPart> taskParts = new HashSet<>();
        List<Worker> workers = workerService.getAvailableWorkers().stream().toList();
        String taskText = new String(task.getFile());

        int workersCount = workers.size();
        int currentStart = 0;
        int delta = taskText.length() / workersCount;

        for (int i = 0; i < workersCount; ++i) {
            if (currentStart == taskText.length()) {
                break;
            }

            int currentEnd = Math.min(currentStart + delta, taskText.length() - 1);
            while (currentEnd < taskText.length() && !END_SENTENCE_CHARS.contains(taskText.charAt(currentEnd))) {
                ++currentEnd;
            }
            if (currentEnd < taskText.length() - 1) {
                ++currentEnd;
            }

            TaskPart taskPart = new TaskPart();
            taskPart.setTask(task);
            taskPart.setOrdinal(i);
            taskPart.setWorker(workers.get(i));
            taskPart.setText(taskText.substring(currentStart, currentEnd));
            taskPart.setIsCompleted(false);

            taskParts.add(taskPart);

            currentStart = currentEnd;
        }

        return new HashSet<>((Collection<TaskPart>) taskPartRepository.saveAll(taskParts));
    }

    public void addTaskPartResult(TaskPartResultDto taskPartResultDto) {
        TaskPart taskPart = taskPartRepository.findById(taskPartResultDto.getTaskPartId()).orElseThrow();
        taskPart.setResult(taskPartResultDto);
        taskPart.setIsCompleted(true);
        taskPartRepository.save(taskPart);
    }

    public int getTaskIdByTaskPartId(int taskPartId) {
        return taskPartRepository.findById(taskPartId).orElseThrow().getTask().getId();
    }

    public Collection<TaskPart> getTaskPartsByTask(Task task) {
        return taskPartRepository.getTaskPartByTask(task);
    }

    public boolean taskIsCompleted(Task task) {
        Collection<TaskPart> taskParts = getTaskPartsByTask(task);
        return taskParts.parallelStream().allMatch(TaskPart::getIsCompleted);
    }

    public void removeTaskPartsByTask(Task task) {
        taskPartRepository.removeAllByTask(task);
    }
}
