package ru.itmo.textanalyzer.jmorphy.core.unit.word;

import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;

import java.util.ArrayList;
import java.util.List;

public class PrefixedParsedWord extends AnalyzerParsedWord {

    private final String prefix;
    private final ParsedWord parsedWord;

    public PrefixedParsedWord(String prefix, ParsedWord p, float score) {
        super(prefix + p.word, p.tag, prefix + p.normalForm, p.foundWord, score);
        this.prefix = prefix;
        this.parsedWord = p;
    }

    @Override
    public ParsedWord rescore(float newScore) {
        return new PrefixedParsedWord(prefix, parsedWord, newScore);
    }

    @Override
    public List<ParsedWord> getLexeme() {
        List<ParsedWord> lexeme = new ArrayList<>();
        for (ParsedWord p : parsedWord.getLexeme()) {
            lexeme.add(new PrefixedParsedWord(prefix, p, 1.0f));
        }
        return lexeme;
    }
}