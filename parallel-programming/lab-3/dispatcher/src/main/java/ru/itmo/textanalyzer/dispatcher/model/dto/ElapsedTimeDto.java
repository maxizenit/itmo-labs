package ru.itmo.textanalyzer.dispatcher.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ElapsedTimeDto {

    private double averageElapsedTime;
    private Map<Integer, Long> elapsedTimeByTasks;
}
