package ru.itmo.textanalyzer.dispatcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itmo.textanalyzer.dispatcher.exception.TaskNotCompletedException;
import ru.itmo.textanalyzer.dispatcher.exception.TaskNotFoundException;
import ru.itmo.textanalyzer.dispatcher.model.dto.ElapsedTimeDto;
import ru.itmo.textanalyzer.dispatcher.model.dto.TaskResultDto;
import ru.itmo.textanalyzer.dispatcher.model.enm.TaskStatus;
import ru.itmo.textanalyzer.dispatcher.model.entity.Task;
import ru.itmo.textanalyzer.dispatcher.service.TaskService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Integer> createTask(@RequestParam MultipartFile file, @RequestParam String nameReplacement) {
        Task task = taskService.createNewTask(file, nameReplacement);
        return ResponseEntity.status(HttpStatus.CREATED).body(task.getId());
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<TaskResultDto> getTaskResult(@PathVariable Integer id) {
        Task task = taskService.getTaskById(id);
        if (!TaskStatus.COMPLETED.equals(task.getStatus())) {
            throw new TaskNotCompletedException();
        }
        return ResponseEntity.ok(task.getResult());
    }

    @GetMapping("/elapsedTime")
    @SuppressWarnings("all")
    public ResponseEntity<ElapsedTimeDto> getElapsedTimeForAllCompletedTasks() {
        Set<Task> tasks = taskService.getTasksByStatus(TaskStatus.COMPLETED);
        Map<Integer, Long> elapsedTimeByTasks =
                tasks.stream().collect(Collectors.toMap(Task::getId, task -> task.getResult().getElapsedMillis()));
        double averageElapsedTime = elapsedTimeByTasks.values().stream().mapToLong(e -> e).average().getAsDouble();

        ElapsedTimeDto result = new ElapsedTimeDto();
        result.setAverageElapsedTime(averageElapsedTime);
        result.setElapsedTimeByTasks(elapsedTimeByTasks);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TaskNotCompletedException.class)
    public ResponseEntity<?> handleTaskNotCompletedException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
