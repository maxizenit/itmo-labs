package ru.itmo.textanalyzer.worker.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class WordsSplitter {

    public List<String> splitIntoWords(String text) {
        List<String> words = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                stringBuilder.append(Character.toLowerCase(c));
            } else if ((Set.of('-', '\'', '\\').contains(c) || Character.isDigit(c)) && !stringBuilder.isEmpty()) {
                stringBuilder.append(c);
            } else if (!stringBuilder.isEmpty()) {
                words.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        }

        if (!stringBuilder.isEmpty()) {
            words.add(stringBuilder.toString());
        }

        return words;
    }
}
