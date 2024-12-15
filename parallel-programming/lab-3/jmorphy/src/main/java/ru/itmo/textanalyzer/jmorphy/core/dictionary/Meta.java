package ru.itmo.textanalyzer.jmorphy.core.dictionary;

import java.util.List;
import java.util.Map;

public class Meta {

    public static final String FORMAT_VERSION = "2.4";

    public final String formatVersion;
    public final String pymorphy2Version;
    public final String languageCode;
    public final String compiledAt;
    public final String source;
    public final String sourceVersion;
    public final String sourceRevision;
    public final long sourceLexemesCount;
    public final long sourceLinksCount;
    public final long gramtabLength;
    public final Map<String, String> gramtabFormats;
    public final long paradigmsLength;
    public final long suffixesLength;
    public final long wordsDawgLength;
    public final CompileOptions compileOptions;
    public final Long[] predictionSuffixesDawgLengths;
    public final boolean ptw;
    public final long ptwUniqueWords;
    public final long ptwOutcomes;
    public final long ptwMinWordFreq;
    public final String corpusRevision;

    public static class CompileOptions {

        public final long maxSuffixLength;
        public final String[] paradigmPrefixes;
        public final long minEndingFreq;
        public final long minParadigmPopularity;

        @SuppressWarnings("unchecked")
        public CompileOptions(Map<String, Object> options) {
            maxSuffixLength = (long) options.get("max_suffix_length");
            paradigmPrefixes = ((List<String>) options.get("paradigm_prefixes")).toArray(new String[0]);
            minEndingFreq = (long) options.get("min_ending_freq");
            minParadigmPopularity = (long) options.get("min_paradigm_popularity");
        }
    }

    @SuppressWarnings("unchecked")
    public Meta(Map<String, Object> meta) {
        formatVersion = (String) meta.get("format_version");
        if (!formatVersion.equals(FORMAT_VERSION)) {
            throw new RuntimeException(String.format("Unsupported format version: %s, expected %s",
                    formatVersion,
                    FORMAT_VERSION));
        }
        pymorphy2Version = (String) meta.get("pymorphy2_version");
        languageCode = ((String) meta.get("language_code")).toLowerCase();
        compiledAt = (String) meta.get("compile_at");
        source = (String) meta.get("source");
        sourceVersion = (String) meta.get("source_version");
        sourceRevision = (String) meta.get("source_revision");
        sourceLexemesCount = (long) meta.get("source_lexemes_count");
        sourceLinksCount = (long) meta.get("source_links_count");
        gramtabLength = (long) meta.get("gramtab_length");
        gramtabFormats = (Map<String, String>) meta.get("gramtab_formats");
        paradigmsLength = (long) meta.get("paradigms_length");
        suffixesLength = (long) meta.get("suffixes_length");
        wordsDawgLength = (long) meta.get("words_dawg_length");
        compileOptions = new CompileOptions((Map<String, Object>) meta.get("compile_options"));
        predictionSuffixesDawgLengths =
                ((List<Long>) meta.get("prediction_suffixes_dawg_lengths")).toArray(new Long[0]);
        ptw = (boolean) (meta.getOrDefault("P(t|w)", false));
        ptwUniqueWords = (long) (meta.getOrDefault("P(t|w)_unique_words", -1L));
        ptwOutcomes = (long) (meta.getOrDefault("P(t|w)_outcomes", -1L));
        ptwMinWordFreq = (long) (meta.getOrDefault("P(t|w)_min_word_freq", -1L));
        corpusRevision = (String) (meta.getOrDefault("corpus_revision", ""));
    }
}
