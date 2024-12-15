package ru.itmo.textanalyzer.jmorphy.core.unit;

import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;
import ru.itmo.textanalyzer.jmorphy.core.unit.word.AnalyzerParsedWord;

import java.util.List;

public class UnknownUnit extends AnalyzerUnit {

    private UnknownUnit(TagStorage tagStorage, boolean terminate, float score) {
        super(tagStorage, terminate, score);
    }

    public static class Builder extends AnalyzerUnit.Builder {

        public Builder(boolean terminate, float score) {
            super(terminate, score);
        }

        @Override
        protected AnalyzerUnit newAnalyzerUnit(TagStorage tagStorage) {
            tagStorage.newGrammeme(List.of("UNKN", "", "НЕИЗВ", "неизвестное"));
            tagStorage.newTag("UNKN");
            return new UnknownUnit(tagStorage, terminate, score);
        }
    }

    @Override
    public List<ParsedWord> parse(String word, String wordLower) {
        return List.of(new AnalyzerParsedWord(word, tagStorage.getTag("UNKN"), word, word, score));
    }
}
