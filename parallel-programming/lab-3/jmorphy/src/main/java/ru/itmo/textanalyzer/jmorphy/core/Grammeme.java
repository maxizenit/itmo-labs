package ru.itmo.textanalyzer.jmorphy.core;

import java.util.List;
import java.util.Objects;

public class Grammeme {

    public final String key;
    public final String value;
    public final String parentValue;
    public final String russianValue;
    public final String description;

    private final TagStorage storage;

    public Grammeme(List<String> grammemeInfo, TagStorage storage) {
        this(grammemeInfo.get(0), grammemeInfo.get(1), grammemeInfo.get(2), grammemeInfo.get(3), storage);
    }

    public Grammeme(String value, String parentValue, String russianValue, String description, TagStorage storage) {
        this.key = TagStorage.normalizeGrammemeValue(value);
        this.value = value;
        this.parentValue = stringOrNull(parentValue);
        this.russianValue = russianValue;
        this.description = description;
        this.storage = storage;
    }

    private String stringOrNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return s;
    }

    public Grammeme getParent() {
        return storage.getGrammeme(parentValue);
    }

    public Grammeme getRoot() {
        Grammeme grammeme = this;
        Grammeme parentGrammeme = grammeme.getParent();
        if (parentGrammeme == null) {
            return null;
        }
        while (parentGrammeme != null) {
            grammeme = parentGrammeme;
            parentGrammeme = grammeme.getParent();
        }
        return grammeme;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Grammeme other) {
            return key.equals(other.key) && storage == other.storage;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return value;
    }

    public String info() {
        return String.format("<%s, %s, %s, %s>", value, parentValue, russianValue, description);
    }
}
