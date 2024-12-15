package ru.itmo.textanalyzer.jmorphy.core.unit;

import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.Tag;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;
import ru.itmo.textanalyzer.jmorphy.core.unit.word.AnalyzerParsedWord;

import java.util.ArrayList;
import java.util.List;

public class NumberUnit extends AnalyzerUnit {

    private NumberUnit(TagStorage tagStorage, boolean terminate, float score) {
        super(tagStorage, terminate, score);
    }

    public static class Builder extends AnalyzerUnit.Builder {

        public Builder(boolean terminate, float score) {
            super(terminate, score);
        }

        @Override
        protected AnalyzerUnit newAnalyzerUnit(TagStorage tagStorage) {
            tagStorage.newGrammeme(List.of("NUMB", "", "ЧИСЛО", "число"));
            tagStorage.newGrammeme(List.of("intg", "", "цел", "целое"));
            tagStorage.newGrammeme(List.of("real", "", "вещ", "вещественное"));
            tagStorage.newTag("NUMB,intg");
            tagStorage.newTag("NUMB,real");
            return new NumberUnit(tagStorage, terminate, score);
        }
    }

    @Override
    public List<ParsedWord> parse(String word, String wordLower) {
        Tag tag = null;
        try {
            Integer.parseInt(word);
            tag = tagStorage.getTag("NUMB,intg");
        } catch (NumberFormatException erInt) {
            try {
                Float.parseFloat(word);
                tag = tagStorage.getTag("NUMB,real");
            } catch (NumberFormatException ignored) {
            }
        }

        if (tag != null) {
            List<ParsedWord> parseds = new ArrayList<>();
            parseds.add(new AnalyzerParsedWord(word, tag, word, word, score));
            return parseds;
        }
        return null;
    }
}
