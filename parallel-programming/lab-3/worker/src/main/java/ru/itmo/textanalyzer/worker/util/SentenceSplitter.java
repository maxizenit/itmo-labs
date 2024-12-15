package ru.itmo.textanalyzer.worker.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SentenceSplitter {

    private static final String REG_EXP = "(?<=[.!?])\\s+";

    public List<String> splitIntoSentences(String text) {
        text = text.replaceAll("\\s*\\n\\s*", " ");
        String[] parts = text.split(REG_EXP);
        List<String> sentences = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                sentences.add(trimmed);
            }
        }
        return sentences;
    }
}
