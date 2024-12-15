package ru.itmo.textanalyzer.dispatcher.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.itmo.textanalyzer.commons.dto.TaskPartResultDto;
import ru.itmo.textanalyzer.commons.dto.TonalityDto;
import ru.itmo.textanalyzer.dispatcher.exception.TaskNotFoundException;
import ru.itmo.textanalyzer.dispatcher.model.dto.TaskResultDto;
import ru.itmo.textanalyzer.dispatcher.model.enm.TaskStatus;
import ru.itmo.textanalyzer.dispatcher.model.entity.Task;
import ru.itmo.textanalyzer.dispatcher.model.entity.TaskPart;
import ru.itmo.textanalyzer.dispatcher.repository.TaskRepository;
import ru.itmo.textanalyzer.dispatcher.service.jms.sender.TaskPartsSender;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskPartService taskPartService;
    private final TaskPartsSender taskPartsSender;

    private final Queue<Task> tasksQueue = new LinkedList<>();
    private final AtomicBoolean isBusy = new AtomicBoolean(false);
    private final AtomicBoolean onCalculatingResult = new AtomicBoolean(false);
    private final Map<Integer, AtomicBoolean> taskIdCompletedMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public Task createNewTask(MultipartFile file, String nameReplacement) {
        Task task = new Task();
        task.setFile(file.getBytes());
        task.setNameReplacement(nameReplacement);
        task.setStatus(TaskStatus.READY_FOR_EXECUTION);
        taskRepository.save(task);
        tasksQueue.add(task);
        log.info("Created new task with ID {}", task.getId());
        return task;
    }

    public Task getTaskById(int id) {
        return taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
    }

    public Set<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findTasksByStatus(status);
    }

    @Scheduled(fixedRate = 1000)
    public void tryExecuteOneTask() {
        log.debug("Trying to execute one task");
        if (isBusy.get() || tasksQueue.isEmpty()) {
            return;
        }
        executeTask(tasksQueue.poll());
    }

    private void executeTask(Task task) {
        if (isBusy.getAndSet(true)) {
            return;
        }
        log.info("Executing task with ID {}", task.getId());

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(new Timestamp(System.currentTimeMillis()));
        task.setParts(taskPartService.createTaskParts(task));
        task = taskRepository.save(task);

        taskIdCompletedMap.put(task.getId(), new AtomicBoolean(false));
        task.getParts().parallelStream().forEach(taskPartsSender::sendTaskPart);
    }

    @Transactional
    public void tryCalculateResult(int taskId) {
        if (taskIdCompletedMap.get(taskId).get()) {
            return;
        }

        Task task = getTaskById(taskId);
        if (!taskPartService.taskIsCompleted(task)) {
            return;
        }
        if (!onCalculatingResult.compareAndSet(false, true)) {
            return;
        }

        log.info("Calculate result for task with ID {}", task.getId());
        Collection<TaskPart> taskParts = taskPartService.getTaskPartsByTask(task);
        List<TaskPartResultDto> taskPartsResults = taskParts.parallelStream().map(TaskPart::getResult).toList();
        int wordsCountSum = taskPartsResults.parallelStream().mapToInt(TaskPartResultDto::getWordsCount).sum();
        Map<String, Integer> mergedWordFrequencyMaps = mergeWordFrequencyMaps(taskPartsResults.parallelStream()
                .map(TaskPartResultDto::getWordsFrequency)
                .toList());
        List<String> mergedSortedSentencesLists = mergeSortedSentencesLists(taskPartsResults.parallelStream()
                .map(TaskPartResultDto::getSortedSentences)
                .toList());
        int positiveWordsCount = taskPartsResults.parallelStream()
                .mapToInt(taskPartResult -> taskPartResult.getTonality().getPositiveWordsCount())
                .sum();
        int negativeWordsCount = taskPartsResults.parallelStream()
                .mapToInt(taskPartResult -> taskPartResult.getTonality().getNegativeWordsCount())
                .sum();
        TonalityDto tonality = new TonalityDto();
        tonality.setPositiveWordsCount(positiveWordsCount);
        tonality.setNegativeWordsCount(negativeWordsCount);
        String textAfterReplaceNames = mergeTextAfterReplaceNames(taskPartsResults);

        TaskResultDto result = new TaskResultDto();
        result.setWordsCount(wordsCountSum);
        result.setWordsFrequency(mergedWordFrequencyMaps);
        result.setTonality(tonality);
        result.setTextAfterReplaceNames(textAfterReplaceNames);
        result.setSortedSentences(mergedSortedSentencesLists);

        task.setEndTime(new Timestamp(System.currentTimeMillis()));
        result.setElapsedMillis(Duration.between(task.getStartTime().toInstant(), task.getEndTime().toInstant())
                .toMillis());

        task.setResult(result);
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
        log.info("Task with ID {} is completed", task.getId());
        taskPartService.removeTaskPartsByTask(task);

        taskIdCompletedMap.get(task.getId()).set(true);
        onCalculatingResult.set(false);
        isBusy.set(false);
    }

    private Map<String, Integer> mergeWordFrequencyMaps(Collection<Map<String, Integer>> wordFrequencyMaps) {
        Map<String, Integer> mergedMap = new ConcurrentHashMap<>();
        wordFrequencyMaps.parallelStream()
                .forEach(map -> map.forEach((key, value) -> mergedMap.merge(key, value, Integer::sum)));
        return mergedMap;
    }

    private List<String> mergeSortedSentencesLists(Collection<List<String>> sortedSentencesLists) {
        List<String> result = new ArrayList<>(sortedSentencesLists.parallelStream().flatMap(List::stream).toList());
        result.sort(Comparator.comparingInt(String::length).reversed());
        return result;
    }

    private String mergeTextAfterReplaceNames(Collection<TaskPartResultDto> taskPartsResults) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < taskPartsResults.size(); ++i) {
            final int finalI = i;
            sb.append(taskPartsResults.parallelStream()
                    .filter(tpr -> tpr.getOrdinal() == finalI)
                    .findFirst()
                    .orElseThrow()
                    .getTextAfterReplaceNames());
        }
        return sb.toString();
    }
}