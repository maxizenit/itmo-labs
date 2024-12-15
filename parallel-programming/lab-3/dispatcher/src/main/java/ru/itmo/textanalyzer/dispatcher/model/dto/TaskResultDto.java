package ru.itmo.textanalyzer.dispatcher.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.itmo.textanalyzer.commons.dto.TonalityDto;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TaskResultDto {

    private Long elapsedMillis;
    private Integer wordsCount;
    private Map<String, Integer> wordsFrequency;
    private TonalityDto tonality;
    private String textAfterReplaceNames;
    private List<String> sortedSentences;
}
