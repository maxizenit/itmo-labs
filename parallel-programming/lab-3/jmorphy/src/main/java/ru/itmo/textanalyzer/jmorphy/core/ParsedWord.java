package ru.itmo.textanalyzer.jmorphy.core;

import lombok.RequiredArgsConstructor;
import ru.itmo.textanalyzer.jmorphy.core.model.Unique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class ParsedWord implements Comparable<ParsedWord> {

    public static final float EPS = 1e-6f;

    public final String word;
    public final Tag tag;
    public final String normalForm;
    public final String foundWord;
    public final float score;

    public abstract ParsedWord rescore(float newScore);

    public abstract List<ParsedWord> getLexeme();

    public List<ParsedWord> inflect(Collection<Grammeme> requiredGrammemes) {
        return inflect(requiredGrammemes, Collections.emptyList());
    }

    public List<ParsedWord> inflect(Collection<Grammeme> requiredGrammemes, Collection<Grammeme> excludeGrammemes) {
        List<ParsedWord> paradigm = new ArrayList<>();
        for (ParsedWord p : getLexeme()) {
            if (p.tag.containsAll(requiredGrammemes) && !p.tag.containsAny(excludeGrammemes)) {
                paradigm.add(p);
            }
        }
        return paradigm;
    }

    @Override
    public String toString() {
        return "<ParsedWord: \"%s\", \"%s\", \"%s\", \"%s\", %.6f>".formatted(word, tag, normalForm, foundWord, score);
    }

    @Override
    public int compareTo(ParsedWord other) {
        return Float.compare(score, other.score);
    }

    public Unique toUnique() {
        return new Unique(tag, normalForm);
    }
}
