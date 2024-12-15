package ru.itmo.textanalyzer.jmorphy.core.model;

import ru.itmo.textanalyzer.jmorphy.core.ParsedWord;

public record ParsedWordWithPrefixId(ParsedWord parsedWord, int prefixId) {
}
