package ru.itmo.textanalyzer.jmorphy.core.unit.word;

import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.Tag;
import ru.itmo.textanalyzer.jmorphy.core.unit.AnalyzerUnit;

import java.util.Collections;
import java.util.List;

public class AnalyzerParsedWord extends ParsedWord {

    public AnalyzerParsedWord(String word, Tag tag, String normalForm, String foundWord, float score) {
        super(word, tag, normalForm, foundWord, score);
    }

    @Override
    public ParsedWord rescore(float newScore) {
        return new AnalyzerParsedWord(word, tag, normalForm, foundWord, newScore);
    }

    @Override
    public List<ParsedWord> getLexeme() {
        return Collections.singletonList(this);
    }

    @Override
    public String toString() {
        return "<ParsedWord: \"%s\", \"%s\", \"%s\", \"%s\", %.6f, %s>".formatted(word,
                tag,
                normalForm,
                foundWord,
                score,
                AnalyzerUnit.class);
    }
}