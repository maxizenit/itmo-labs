package ru.itmo.textanalyzer.jmorphy.core;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TagStorage {

    private final Map<String, Tag> tags = new HashMap<>();
    private final Map<String, Grammeme> grammemes = new HashMap<>();

    static String normalizeGrammemeValue(String grammemeValue) {
        return grammemeValue.toLowerCase();
    }

    public static String[] splitTagString(String tagString) {
        return tagString.replace(" ", ",").split(",");
    }

    private static String normalizeTagString(String tagString) {
        String[] grammemeStrings = splitTagString(tagString);
        List<String> normalizedGrammemeValues = new ArrayList<>(grammemeStrings.length);
        for (String grammemeValue : grammemeStrings) {
            normalizedGrammemeValues.add(normalizeGrammemeValue(grammemeValue));
        }
        Collections.sort(normalizedGrammemeValues);
        return String.join(" ", normalizedGrammemeValues);
    }

    public Tag getTag(String tagString) {
        return tags.get(normalizeTagString(tagString));
    }

    public Collection<Tag> getAllTags() {
        return tags.values();
    }

    private void addTag(Tag tag) {
        tags.put(tag.getNormalizedTagString(), tag);
    }

    public Tag newTag(String tagString) {
        Tag tag = getTag(tagString);
        if (tag == null) {
            tag = new Tag(tagString, this);
            addTag(tag);
        }
        return tag;
    }

    public Grammeme getGrammeme(String grammemeValue) {
        if (grammemeValue == null) {
            return null;
        }
        return grammemes.get(normalizeGrammemeValue(grammemeValue));
    }

    public Collection<Grammeme> getAllGrammemes() {
        return grammemes.values();
    }

    private void addGrammeme(Grammeme grammeme) {
        grammemes.put(grammeme.key, grammeme);
    }

    public Grammeme newGrammeme(List<String> grammemeInfo) {
        Grammeme grammeme = getGrammeme(grammemeInfo.getFirst());
        if (grammeme == null) {
            grammeme = new Grammeme(grammemeInfo, this);
            addGrammeme(grammeme);
        }
        return grammeme;
    }
}
