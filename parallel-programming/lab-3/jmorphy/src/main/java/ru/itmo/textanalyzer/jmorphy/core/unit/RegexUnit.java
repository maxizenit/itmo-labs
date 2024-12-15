package ru.itmo.textanalyzer.jmorphy.core.unit;

import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;
import ru.itmo.textanalyzer.jmorphy.core.unit.word.AnalyzerParsedWord;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexUnit extends AnalyzerUnit {

    protected final Pattern pattern;
    protected final String tagString;

    public RegexUnit(TagStorage tagStorage, String regex, String tagString, boolean terminate, float score) {
        super(tagStorage, terminate, score);
        this.pattern = Pattern.compile(regex);
        this.tagString = tagString;
    }

    @Override
    public List<ParsedWord> parse(String word, String wordLower) {
        if (pattern.matcher(word).matches()) {
            List<ParsedWord> parseds = new ArrayList<>();
            parseds.add(new AnalyzerParsedWord(word, tagStorage.getTag(tagString), word, word, score));
            return parseds;
        }
        return null;
    }
}
