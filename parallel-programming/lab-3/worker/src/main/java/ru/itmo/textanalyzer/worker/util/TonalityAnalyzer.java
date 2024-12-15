package ru.itmo.textanalyzer.worker.util;

import org.springframework.stereotype.Component;
import ru.itmo.textanalyzer.commons.dto.TonalityDto;

import java.util.List;

@Component
public class TonalityAnalyzer {

    public TonalityDto analyze(List<String> words) {
        int positiveWords = 0;
        int negativeWords = 0;

        for (String word : words) {
            if (TonalityAnalyzerConstants.POSITIVE_WORDS.contains(word)) {
                ++positiveWords;
            } else if (TonalityAnalyzerConstants.NEGATIVE_WORDS.contains(word)) {
                ++negativeWords;
            }
        }

        TonalityDto result = new TonalityDto();
        result.setPositiveWordsCount(positiveWords);
        result.setNegativeWordsCount(negativeWords);
        return result;
    }
}
