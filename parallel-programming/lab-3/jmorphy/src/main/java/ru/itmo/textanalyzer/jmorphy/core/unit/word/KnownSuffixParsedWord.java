package ru.itmo.textanalyzer.jmorphy.core.unit.word;

import ru.itmo.textanalyzer.jmorphy.core.Dictionary;
import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.Tag;
import ru.itmo.textanalyzer.jmorphy.core.dictionary.Paradigm;
import ru.itmo.textanalyzer.jmorphy.dawg.SuffixesDawg;

import java.util.ArrayList;
import java.util.List;

public class KnownSuffixParsedWord extends AnalyzerParsedWord {

    private final SuffixesDawg.SuffixForm suffixForm;
    private final Dictionary dict;

    public KnownSuffixParsedWord(String word,
                                 Tag tag,
                                 String normalForm,
                                 String foundWord,
                                 SuffixesDawg.SuffixForm suffixForm,
                                 float score,
                                 Dictionary dict) {
        super(word, tag, normalForm, foundWord, score);
        this.suffixForm = suffixForm;
        this.dict = dict;
    }

    @Override
    public ParsedWord rescore(float newScore) {
        return new KnownSuffixParsedWord(word, tag, normalForm, foundWord, suffixForm, newScore, dict);
    }

    @Override
    public List<ParsedWord> getLexeme() {
        List<ParsedWord> lexeme = new ArrayList<>();
        Paradigm paradigm = dict.getParadigm(suffixForm.paradigmId());
        int paradigmSize = paradigm.size();
        String wordPrefix = word.substring(0, word.length() - suffixForm.word().length());
        String stem = wordPrefix + dict.buildStem(suffixForm.paradigmId(), suffixForm.idx(), suffixForm.word());
        String normalForm =
                wordPrefix + dict.buildNormalForm(suffixForm.paradigmId(), suffixForm.idx(), suffixForm.word());
        for (short idx = 0; idx < paradigmSize; idx++) {
            String prefix = dict.getParadigmPrefixes()[paradigm.getStemPrefixId(idx)];
            String suffix = dict.getSuffix(suffixForm.paradigmId(), idx);
            String word = prefix + stem + suffix;
            Tag tag = dict.buildTag(suffixForm.paradigmId(), idx);
            SuffixesDawg.SuffixForm sf =
                    new SuffixesDawg.SuffixForm(suffixForm.word(), (short) 0, suffixForm.paradigmId(), idx);
            lexeme.add(new KnownSuffixParsedWord(word, tag, normalForm, sf.word(), sf, score, dict));
        }
        return lexeme;
    }
}