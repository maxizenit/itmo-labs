package ru.itmo.textanalyzer.jmorphy.core.unit;

import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;
import ru.itmo.textanalyzer.jmorphy.core.unit.word.PrefixedParsedWord;

import java.util.ArrayList;
import java.util.List;

abstract class PrefixedUnit extends AnalyzerUnit {

    protected final AnalyzerUnit unit;

    public PrefixedUnit(TagStorage tagStorage, AnalyzerUnit unit, boolean terminate, float score) {
        super(tagStorage, terminate, score);
        this.unit = unit;
    }

    protected List<ParsedWord> parseWithPrefix(String word, String wordLower, String prefix) {
        List<ParsedWord> parseds = new ArrayList<>();
        int prefixLen = prefix.length();
        for (ParsedWord p : unit.parse(word.substring(prefixLen), wordLower.substring(prefixLen))) {
            if (!p.tag.isProductive()) {
                continue;
            }
            parseds.add(new PrefixedParsedWord(prefix, p, score));
        }
        return parseds;
    }
}
