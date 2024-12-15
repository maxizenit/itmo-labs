package ru.itmo.textanalyzer.dispatcher.model.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.textanalyzer.commons.dto.TaskPartResultDto;
import ru.itmo.textanalyzer.dispatcher.model.converter.TaskPartResultDtoConverter;

@Getter
@Setter
@Entity
public class TaskPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Nonnull
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Nonnull
    private Integer ordinal;

    @Nonnull
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @Nonnull
    private String text;

    @Nonnull
    private Boolean isCompleted;

    @Convert(converter = TaskPartResultDtoConverter.class)
    private TaskPartResultDto result;
}
