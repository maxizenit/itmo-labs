package ru.itmo.textanalyzer.jmorphy.core.model;

import ru.itmo.textanalyzer.jmorphy.core.ProbabilityEstimator;
import ru.itmo.textanalyzer.jmorphy.core.unit.AnalyzerUnit;

import java.util.List;

public record MorphAnalyzerUnits(List<AnalyzerUnit> units, ProbabilityEstimator probabilityEstimator) {
}