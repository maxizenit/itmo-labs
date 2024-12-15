package ru.itmo.textanalyzer.worker.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.textanalyzer.jmorphy.MorphAnalyzer;
import ru.itmo.textanalyzer.jmorphy.core.Tag;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NamesFinder {

    private final MorphAnalyzer morphAnalyzer;

    public Set<String> findAllNames(List<String> words) {
        return words.stream().filter(this::isName).collect(Collectors.toSet());
    }

    public boolean isName(String word) {
        Set<String> grammemes = morphAnalyzer.tag(word)
                .stream()
                .map(Tag::getGrammemeValues)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        return grammemes.contains("Name");
    }
}
