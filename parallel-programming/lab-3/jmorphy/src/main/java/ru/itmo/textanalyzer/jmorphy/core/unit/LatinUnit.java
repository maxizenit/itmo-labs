package ru.itmo.textanalyzer.jmorphy.core.unit;

import ru.itmo.textanalyzer.jmorphy.core.TagStorage;

import java.util.List;

public class LatinUnit extends RegexUnit {

    private static final String LATIN_REGEX = "[\\p{IsLatin}\\d\\p{Punct}]+";

    public LatinUnit(TagStorage tagStorage, boolean terminate, float score) {
        super(tagStorage, LATIN_REGEX, "LATN", terminate, score);
    }

    public static class Builder extends AnalyzerUnit.Builder {

        public Builder(boolean terminate, float score) {
            super(terminate, score);
        }

        @Override
        protected AnalyzerUnit newAnalyzerUnit(TagStorage tagStorage) {
            tagStorage.newGrammeme(List.of("LATN", "", "ЛАТ", "латиница"));
            tagStorage.newTag("LATN");
            return new LatinUnit(tagStorage, terminate, score);
        }
    }
}
