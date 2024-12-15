package ru.itmo.textanalyzer.jmorphy;

import ru.itmo.textanalyzer.jmorphy.core.ProbabilityEstimator;
import ru.itmo.textanalyzer.jmorphy.core.Resources;
import ru.itmo.textanalyzer.jmorphy.core.TagStorage;
import ru.itmo.textanalyzer.jmorphy.core.dictionary.Meta;
import ru.itmo.textanalyzer.jmorphy.core.model.MorphAnalyzerUnits;
import ru.itmo.textanalyzer.jmorphy.core.unit.AnalyzerUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.DictionaryUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.KnownPrefixUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.KnownSuffixUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.LatinUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.NumberUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.PunctuationUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.RomanUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.UnknownPrefixUnit;
import ru.itmo.textanalyzer.jmorphy.core.unit.UnknownUnit;
import ru.itmo.textanalyzer.jmorphy.core.util.fileloader.FSFileLoader;
import ru.itmo.textanalyzer.jmorphy.core.util.fileloader.FileLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MorphAnalyzerBuilder {

    private static final String DICT_PATH_VAR = "dictPath";

    private final TagStorage tagStorage = new TagStorage();

    private String dictPath;
    private FileLoader loader;
    private Map<Character, String> charSubstitutes;
    private List<AnalyzerUnit.Builder> unitBuilders;

    public MorphAnalyzerBuilder dictPath(String path) {
        this.dictPath = path;
        return this;
    }

    public MorphAnalyzerBuilder fileLoader(FileLoader loader) {
        this.loader = loader;
        return this;
    }

    public MorphAnalyzerBuilder charSubstitutes(Map<Character, String> charSubstitutes) {
        this.charSubstitutes = charSubstitutes;
        return this;
    }

    private MorphAnalyzerUnits prepare() throws IOException {
        if (loader == null) {
            if (dictPath == null) {
                dictPath = System.getProperty(DICT_PATH_VAR);
            }
            loader = new FSFileLoader(dictPath);
        }

        if (unitBuilders == null) {
            ru.itmo.textanalyzer.jmorphy.core.Dictionary.Builder dictBuilder =
                    new ru.itmo.textanalyzer.jmorphy.core.Dictionary.Builder(loader);
            String langCode = dictBuilder.build(tagStorage).getMeta().languageCode.toUpperCase();
            Set<String> knownPrefixes = Resources.getKnownPrefixes(langCode);
            if (charSubstitutes == null) {
                charSubstitutes = Resources.getCharSubstitutes(langCode);
            }
            AnalyzerUnit.Builder dictUnitBuilder =
                    new DictionaryUnit.Builder(dictBuilder, true, 1.0f).charSubstitutes(charSubstitutes);
            unitBuilders = new ArrayList<>();
            unitBuilders.add(dictUnitBuilder);
            unitBuilders.add(new NumberUnit.Builder(true, 0.9f));
            unitBuilders.add(new PunctuationUnit.Builder(true, 0.9f));
            unitBuilders.add(new RomanUnit.Builder(false, 0.9f));
            unitBuilders.add(new LatinUnit.Builder(true, 0.9f));
            if (!knownPrefixes.isEmpty()) {
                unitBuilders.add(new KnownPrefixUnit.Builder(dictUnitBuilder, knownPrefixes, true, 0.75f));
            }
            unitBuilders.add(new UnknownPrefixUnit.Builder(dictUnitBuilder, true, 0.5f));
            unitBuilders.add(new KnownSuffixUnit.Builder(dictBuilder, true, 0.5f).charSubstitutes(charSubstitutes));
            unitBuilders.add(new UnknownUnit.Builder(true, 1.0f));
        }
        List<AnalyzerUnit> units = new ArrayList<>();
        Meta dictMeta = null;
        for (AnalyzerUnit.Builder unitBuilder : unitBuilders) {
            AnalyzerUnit unit = unitBuilder.build(tagStorage);
            if (unit instanceof DictionaryUnit) {
                dictMeta = ((DictionaryUnit) unit).getDict().getMeta();
            }
            units.add(unit);
        }

        ProbabilityEstimator probabilityEstimator = null;
        if (dictMeta != null && dictMeta.ptw) {
            probabilityEstimator = new ProbabilityEstimator(loader);
        }

        return new MorphAnalyzerUnits(units, probabilityEstimator);
    }

    public MorphAnalyzer build() throws IOException {
        var prepared = prepare();
        return new MorphAnalyzer(tagStorage, prepared.units(), prepared.probabilityEstimator());
    }
}
