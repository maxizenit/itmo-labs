package ru.itmo.textanalyzer.jmorphy.core.unit;

import ru.itmo.textanalyzer.jmorphy.core.TagStorage;

import java.util.List;

public class PunctuationUnit extends RegexUnit {

    private static final String PUNCTUATION_REGEX = "\\p{Punct}+";

    private PunctuationUnit(TagStorage tagStorage, boolean terminate, float score) {
        super(tagStorage, PUNCTUATION_REGEX, "PNCT", terminate, score);
    }

    public static class Builder extends AnalyzerUnit.Builder {

        public Builder(boolean terminate, float score) {
            super(terminate, score);
        }

        @Override
        protected AnalyzerUnit newAnalyzerUnit(TagStorage tagStorage) {
            tagStorage.newGrammeme(List.of("PNCT", "", "ЗПР", "пунктуация"));
            tagStorage.newTag("PNCT");
            return new PunctuationUnit(tagStorage, terminate, score);
        }
    }
}
