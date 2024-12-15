package ru.itmo.textanalyzer.worker.util;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WordFrequencyCalculator {

    public Map<String, Integer> calculateWordFrequency(List<String> words) {
        if (words.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> wordsFrequency = new HashMap<>();
        words.forEach(word -> wordsFrequency.put(word, wordsFrequency.getOrDefault(word, 0) + 1));
        return wordsFrequency;
    }
}
