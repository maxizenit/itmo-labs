package ru.itmo.textanalyzer.jmorphy.core.unit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AnalyzerUnit {

    protected final TagStorage tagStorage;

    @Getter
    protected final boolean terminate;

    protected final float score;

    @RequiredArgsConstructor
    public static abstract class Builder {

        protected final boolean terminate;
        protected final float score;
        protected AnalyzerUnit cachedUnit;

        protected abstract AnalyzerUnit newAnalyzerUnit(TagStorage tagStorage) throws IOException;

        public AnalyzerUnit build(TagStorage tagStorage) throws IOException {
            if (cachedUnit == null) {
                cachedUnit = newAnalyzerUnit(tagStorage);
            }
            return cachedUnit;
        }
    }

    public abstract List<ParsedWord> parse(String word, String wordLower);
}
