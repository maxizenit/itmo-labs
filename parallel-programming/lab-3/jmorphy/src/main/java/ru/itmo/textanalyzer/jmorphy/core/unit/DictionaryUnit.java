package ru.itmo.textanalyzer.jmorphy.core.unit;

import lombok.Getter;
import ru.itmo.textanalyzer.jmorphy.core.Dictionary;
import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.Tag;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;
import ru.itmo.textanalyzer.jmorphy.core.unit.word.DictionaryParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.model.WordForm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictionaryUnit extends AnalyzerUnit {

    @Getter
    private final Dictionary dict;

    private final Map<Character, String> charSubstitutes;

    public DictionaryUnit(TagStorage tagStorage,
                          Dictionary dict,
                          Map<Character, String> charSubstitutes,
                          boolean terminate,
                          float score) {
        super(tagStorage, terminate, score);
        this.dict = dict;
        this.charSubstitutes = charSubstitutes;
    }

    public static class Builder extends AnalyzerUnit.Builder {

        private final Dictionary.Builder dictBuilder;
        private Map<Character, String> charSubstitutes;

        public Builder(Dictionary.Builder dictBuilder, boolean terminate, float score) {
            super(terminate, score);
            this.dictBuilder = dictBuilder;
        }

        public Builder charSubstitutes(Map<Character, String> charSubstitutes) {
            this.charSubstitutes = charSubstitutes;
            this.cachedUnit = null;
            return this;
        }

        @Override
        protected AnalyzerUnit newAnalyzerUnit(TagStorage tagStorage) throws IOException {
            Dictionary dict = dictBuilder.build(tagStorage);
            return new DictionaryUnit(tagStorage, dict, charSubstitutes, terminate, score);
        }
    }

    @Override
    public List<ParsedWord> parse(String word, String wordLower) {
        List<ParsedWord> parseds = new ArrayList<>();
        for (WordForm wf : dict.getWords().similarWords(wordLower, charSubstitutes)) {
            String normalForm = dict.buildNormalForm(wf.paradigmId(), wf.idx(), wf.word());
            Tag tag = dict.buildTag(wf.paradigmId(), wf.idx());
            parseds.add(new DictionaryParsedWord(wordLower, tag, normalForm, wf.word(), wf, score, dict));
        }
        return parseds;
    }
}
