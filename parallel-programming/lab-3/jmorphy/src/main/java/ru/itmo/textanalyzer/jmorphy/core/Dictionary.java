package ru.itmo.textanalyzer.jmorphy.core;

import lombok.Getter;
import org.apache.commons.io.input.SwappedDataInputStream;
import ru.itmo.textanalyzer.jmorphy.core.dictionary.Meta;
import ru.itmo.textanalyzer.jmorphy.core.dictionary.Paradigm;
import ru.itmo.textanalyzer.jmorphy.core.util.JSONUtils;
import ru.itmo.textanalyzer.jmorphy.core.util.fileloader.FileLoader;
import ru.itmo.textanalyzer.jmorphy.dawg.SuffixesDawg;
import ru.itmo.textanalyzer.jmorphy.dawg.WordsDawg;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Dictionary {

    @Getter
    private final Meta meta;

    @Getter
    private final String[] paradigmPrefixes;

    @Getter
    private final WordsDawg words;

    private final SuffixesDawg[] predictionSuffixes;
    private final Paradigm[] paradigms;
    private final String[] suffixes;
    private final Tag[] gramtab;

    private Dictionary(Meta meta,
                       WordsDawg words,
                       SuffixesDawg[] predictionSuffixes,
                       Paradigm[] paradigms,
                       String[] suffixes,
                       Tag[] gramtab) {
        this.meta = meta;
        this.paradigmPrefixes = meta.compileOptions.paradigmPrefixes;
        this.words = words;
        this.predictionSuffixes = predictionSuffixes;
        this.paradigms = paradigms;
        this.suffixes = suffixes;
        this.gramtab = gramtab;
    }

    public static class Builder {

        private final FileLoader loader;
        private Dictionary cachedDict;

        public static final String META_FILENAME = "meta.json";
        public static final String WORDS_FILENAME = "words.dawg";
        public static final String PARADIGMS_FILENAME = "paradigms.array";
        public static final String SUFFIXES_FILENAME = "suffixes.json";
        public static final String PREDICTION_SUFFIXES_FILENAME_TEMPLATE = "prediction-suffixes-%s.dawg";
        public static final String GRAMMEMES_FILENAME = "grammemes.json";
        public static final String GRAMTAB_OPENCORPORA_FILENAME = "gramtab-opencorpora-int.json";

        public Builder(FileLoader loader) {
            this.loader = loader;
        }

        @SuppressWarnings("unchecked")
        private Meta parseMeta(InputStream stream) throws IOException {
            Map<String, Object> rawMeta = new HashMap<>();
            List<List<Object>> parsed = (List<List<Object>>) JSONUtils.parseJSON(stream);
            for (List<Object> pair : parsed) {
                rawMeta.put((String) pair.get(0), pair.get(1));
            }
            return new Meta(rawMeta);
        }

        private SuffixesDawg[] parsePredictionSuffixes(FileLoader loader, String filenameTemplate, int num) throws
                IOException {
            SuffixesDawg[] predictionSuffixes = new SuffixesDawg[num];
            for (int i = 0; i < num; i++) {
                InputStream suffixesStream = loader.newStream(String.format(filenameTemplate, i));
                predictionSuffixes[i] = new SuffixesDawg(suffixesStream);
                suffixesStream.close();
            }
            return predictionSuffixes;
        }

        private Paradigm[] parseParadigms(InputStream stream) throws IOException {
            DataInput paradigmsStream = new SwappedDataInputStream(stream);
            short paradigmCount = paradigmsStream.readShort();
            Paradigm[] paradigms = new Paradigm[paradigmCount];
            for (int paraId = 0; paraId < paradigmCount; paraId++) {
                paradigms[paraId] = new Paradigm(paradigmsStream);
            }
            return paradigms;
        }

        @SuppressWarnings("unchecked")
        private String[] parseSuffixes(InputStream stream) throws IOException {
            return ((List<String>) JSONUtils.parseJSON(stream)).toArray(new String[0]);
        }

        @SuppressWarnings("unchecked")
        private void loadGrammemes(TagStorage tagStorage, InputStream stream) throws IOException {
            for (List<String> grammemeInfo : (List<List<String>>) JSONUtils.parseJSON(stream)) {
                tagStorage.newGrammeme(grammemeInfo);
            }
        }

        @SuppressWarnings("unchecked")
        private Tag[] parseGramtab(TagStorage tagStorage, InputStream stream) throws IOException {
            List<String> tagStrings = (List<String>) JSONUtils.parseJSON(stream);
            int tagsLength = tagStrings.size();
            Tag[] gramtab = new Tag[tagsLength];
            for (int i = 0; i < tagsLength; i++) {
                gramtab[i] = tagStorage.newTag(tagStrings.get(i));
            }
            return gramtab;
        }

        public Dictionary build(TagStorage tagStorage) throws IOException {
            if (cachedDict == null) {
                InputStream metaStream = loader.newStream(META_FILENAME);
                Meta meta = parseMeta(metaStream);
                metaStream.close();
                InputStream grammemesStream = loader.newStream(GRAMMEMES_FILENAME);
                loadGrammemes(tagStorage, grammemesStream);
                grammemesStream.close();
                InputStream wordsStream = loader.newStream(WORDS_FILENAME);
                InputStream paradigmsStream = loader.newStream(PARADIGMS_FILENAME);
                InputStream suffixesStream = loader.newStream(SUFFIXES_FILENAME);
                InputStream gramtabStream = loader.newStream(GRAMTAB_OPENCORPORA_FILENAME);
                cachedDict = new Dictionary(meta,
                        new WordsDawg(wordsStream),
                        parsePredictionSuffixes(loader,
                                PREDICTION_SUFFIXES_FILENAME_TEMPLATE,
                                meta.compileOptions.paradigmPrefixes.length),
                        parseParadigms(paradigmsStream),
                        parseSuffixes(suffixesStream),
                        parseGramtab(tagStorage, gramtabStream));
                wordsStream.close();
                paradigmsStream.close();
                suffixesStream.close();
                gramtabStream.close();
            }
            return cachedDict;
        }
    }

    public SuffixesDawg getPredictionSuffixes(int n) {
        return predictionSuffixes[n];
    }

    public Paradigm getParadigm(short paradigmId) {
        return paradigms[paradigmId];
    }

    public String getSuffix(short paradigmId, short idx) {
        return suffixes[paradigms[paradigmId].getStemSuffixId(idx)];
    }

    public Tag buildTag(short paradigmId, short idx) {
        Paradigm paradigm = paradigms[paradigmId];
        return gramtab[paradigm.getTagId(idx)];
    }

    public String buildNormalForm(short paradigmId, short idx, String word) {
        Paradigm paradigm = paradigms[paradigmId];
        String stem = buildStem(paradigmId, idx, word);
        String prefix = paradigmPrefixes[paradigm.getNormPrefixId()];
        String suffix = suffixes[paradigm.getNormSuffixId()];

        return prefix + stem + suffix;
    }

    public String buildStem(short paradigmId, short idx, String word) {
        Paradigm paradigm = paradigms[paradigmId];
        String prefix = paradigmPrefixes[paradigm.getStemPrefixId(idx)];
        String suffix = suffixes[paradigm.getStemSuffixId(idx)];

        if (!suffix.isEmpty()) {
            return word.substring(prefix.length(), word.length() - suffix.length());
        }
        return word.substring(prefix.length());
    }
}
