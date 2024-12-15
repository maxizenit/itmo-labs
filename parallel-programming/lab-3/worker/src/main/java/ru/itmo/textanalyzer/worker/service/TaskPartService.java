package ru.itmo.textanalyzer.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.textanalyzer.commons.dto.TaskPartDto;
import ru.itmo.textanalyzer.commons.dto.TaskPartResultDto;
import ru.itmo.textanalyzer.worker.service.jms.sender.TaskPartResultsSender;
import ru.itmo.textanalyzer.worker.util.NamesFinder;
import ru.itmo.textanalyzer.worker.util.NamesReplacer;
import ru.itmo.textanalyzer.worker.util.SentenceSplitter;
import ru.itmo.textanalyzer.worker.util.TonalityAnalyzer;
import ru.itmo.textanalyzer.worker.util.WordFrequencyCalculator;
import ru.itmo.textanalyzer.worker.util.WordsSplitter;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPartService {

    private final TaskPartResultsSender taskPartResultsSender;
    private final WordsSplitter wordsSplitter;
    private final WordFrequencyCalculator wordFrequencyCalculator;
    private final TonalityAnalyzer tonalityAnalyzer;
    private final NamesFinder namesFinder;
    private final NamesReplacer namesReplacer;
    private final SentenceSplitter sentenceSplitter;

    public void executeTaskPart(TaskPartDto taskPart) {
        String text = taskPart.getText();
        log.info("Executing task part (ID: {}, text length: {})", taskPart.getId(), text.length());

        TaskPartResultDto resultDto = new TaskPartResultDto();
        resultDto.setTaskPartId(taskPart.getId());
        resultDto.setOrdinal(taskPart.getOrdinal());

        List<String> words = wordsSplitter.splitIntoWords(text);

        resultDto.setWordsCount(words.size());
        resultDto.setWordsFrequency(wordFrequencyCalculator.calculateWordFrequency(words));
        resultDto.setTonality(tonalityAnalyzer.analyze(words));

        Set<String> names = namesFinder.findAllNames(words);
        String textAfterReplaceNames = namesReplacer.replaceAllNames(text, names, taskPart.getNameReplacement());
        resultDto.setTextAfterReplaceNames(textAfterReplaceNames);

        resultDto.setSortedSentences(sentenceSplitter.splitIntoSentences(text)
                .stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList());

        taskPartResultsSender.sendTaskPartResult(resultDto);
    }
}
