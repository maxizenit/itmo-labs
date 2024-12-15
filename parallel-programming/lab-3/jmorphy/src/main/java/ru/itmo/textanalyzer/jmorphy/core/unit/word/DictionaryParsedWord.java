package ru.itmo.textanalyzer.jmorphy.core.unit.word;

import ru.itmo.textanalyzer.jmorphy.core.Dictionary;
import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.Tag;
import ru.itmo.textanalyzer.jmorphy.core.dictionary.Paradigm;
import ru.itmo.textanalyzer.jmorphy.core.model.WordForm;

import java.util.ArrayList;
import java.util.List;

public class DictionaryParsedWord extends AnalyzerParsedWord {

    private final WordForm wordForm;
    private final Dictionary dict;

    public DictionaryParsedWord(String word,
                                Tag tag,
                                String normalForm,
                                String foundWord,
                                WordForm wordForm,
                                float score,
                                Dictionary dict) {
        super(word, tag, normalForm, foundWord, score);
        this.wordForm = wordForm;
        this.dict = dict;
    }

    @Override
    public ParsedWord rescore(float newScore) {
        return new DictionaryParsedWord(word, tag, normalForm, foundWord, wordForm, newScore, dict);
    }

    @Override
    public List<ParsedWord> getLexeme() {
        List<ParsedWord> lexeme = new ArrayList<>();
        Paradigm paradigm = dict.getParadigm(wordForm.paradigmId());
        int paradigmSize = paradigm.size();
        String stem = dict.buildStem(wordForm.paradigmId(), wordForm.idx(), wordForm.word());
        String normalForm = dict.buildNormalForm(wordForm.paradigmId(), wordForm.idx(), wordForm.word());
        for (short idx = 0; idx < paradigmSize; idx++) {
            String prefix = dict.getParadigmPrefixes()[paradigm.getStemPrefixId(idx)];
            String suffix = dict.getSuffix(wordForm.paradigmId(), idx);
            String word = prefix + stem + suffix;
            Tag tag = dict.buildTag(wordForm.paradigmId(), idx);
            WordForm wf = new WordForm(word, wordForm.paradigmId(), idx);
            lexeme.add(new DictionaryParsedWord(word, tag, normalForm, wf.word(), wf, score, dict));
        }
        return lexeme;
    }
}
