package ru.itmo.textanalyzer.commons.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TaskPartResultDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer taskPartId;
    private Integer ordinal;
    private Integer wordsCount;
    private Map<String, Integer> wordsFrequency;
    private TonalityDto tonality;
    private String textAfterReplaceNames;
    private List<String> sortedSentences;
}
