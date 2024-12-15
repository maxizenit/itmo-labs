package ru.itmo.textanalyzer.jmorphy;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ru.itmo.textanalyzer.jmorphy.core.Grammeme;
import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;
import ru.itmo.textanalyzer.jmorphy.core.ProbabilityEstimator;
import ru.itmo.textanalyzer.jmorphy.core.Tag;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;
import ru.itmo.textanalyzer.jmorphy.core.model.Unique;
import ru.itmo.textanalyzer.jmorphy.core.unit.AnalyzerUnit;
import ru.itmo.textanalyzer.jmorphy.core.util.fileloader.ResourceFileLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor()
public class MorphAnalyzer {

    private final TagStorage tagStorage;
    private final List<AnalyzerUnit> units;
    private final ProbabilityEstimator prob;

    @SneakyThrows
    public static MorphAnalyzer createMorphAnalyzer() {
        return new MorphAnalyzerBuilder().fileLoader(new ResourceFileLoader("/lang/ru/dict")).build();
    }

    public Grammeme getGrammeme(String value) {
        return tagStorage.getGrammeme(value);
    }

    public Collection<Grammeme> getAllGrammemes() {
        return tagStorage.getAllGrammemes();
    }

    public Tag getTag(String tagString) {
        return tagStorage.getTag(tagString);
    }

    public Collection<Tag> getAllTags() {
        return tagStorage.getAllTags();
    }

    public List<String> normalForms(char[] buffer, int offset, int count) {
        return normalForms(new String(buffer, offset, count));
    }

    public List<String> normalForms(String word) {
        List<ParsedWord> parseds = parse(word);
        List<String> normalForms = new ArrayList<>();
        Set<String> uniqueNormalForms = new HashSet<>();

        for (ParsedWord p : parseds) {
            if (!uniqueNormalForms.contains(p.normalForm)) {
                normalForms.add(p.normalForm);
                uniqueNormalForms.add(p.normalForm);
            }
        }
        return normalForms;
    }

    public List<Tag> tag(String word) {
        List<ParsedWord> parseds = parse(word);
        List<Tag> tags = new ArrayList<>(parseds.size());
        for (ParsedWord p : parseds) {
            tags.add(p.tag);
        }
        return tags;
    }

    public List<ParsedWord> parse(String word) {
        String wordLower = word.toLowerCase();
        List<ParsedWord> parseds = new ArrayList<>();
        for (AnalyzerUnit unit : units) {
            List<ParsedWord> unitParseds = unit.parse(word, wordLower);
            if (unitParseds == null) {
                continue;
            }

            parseds.addAll(unitParseds);
            if (unit.isTerminate() && !parseds.isEmpty()) {
                break;
            }
        }

        parseds = filterDups(parseds);
        parseds = estimate(parseds);
        parseds.sort(Collections.reverseOrder());
        return parseds;
    }

    private List<ParsedWord> estimate(List<ParsedWord> parseds) {
        float[] newScores = new float[parseds.size()];
        float sumProbs = 0.0f, sumScores = 0.0f;
        int i = 0;
        if (prob == null) {
            return parseds;
        }
        for (ParsedWord parsed : parseds) {
            newScores[i] = prob.getProbability(parsed.foundWord, parsed.tag);
            sumProbs += newScores[i];
            sumScores += parsed.score;
            i++;
        }
        if (sumProbs < ParsedWord.EPS) {
            float k = 1.0f / sumScores;
            i = 0;
            for (ParsedWord parsed : parseds) {
                newScores[i] = parsed.score * k;
                i++;
            }
        }

        List<ParsedWord> estimatedParseds = new ArrayList<>(parseds.size());
        i = 0;
        for (ParsedWord parsed : parseds) {
            estimatedParseds.add(parsed.rescore(newScores[i]));
            i++;
        }

        return estimatedParseds;
    }

    private List<ParsedWord> filterDups(List<ParsedWord> parseds) {
        Set<Unique> seenParseds = new HashSet<>();
        List<ParsedWord> filteredParseds = new ArrayList<>();
        for (ParsedWord p : parseds) {
            Unique u = p.toUnique();
            if (!seenParseds.contains(u)) {
                filteredParseds.add(p);
                seenParseds.add(u);
            }
        }
        return filteredParseds;
    }
}