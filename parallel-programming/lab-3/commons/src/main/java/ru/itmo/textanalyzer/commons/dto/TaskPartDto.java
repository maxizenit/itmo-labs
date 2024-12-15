package ru.itmo.textanalyzer.commons.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class TaskPartDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer ordinal;
    private String nameReplacement;
    private String text;
}
