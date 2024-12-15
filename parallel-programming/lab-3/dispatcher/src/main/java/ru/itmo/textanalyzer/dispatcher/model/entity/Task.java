package ru.itmo.textanalyzer.dispatcher.model.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.textanalyzer.dispatcher.model.converter.TaskResultDtoConverter;
import ru.itmo.textanalyzer.dispatcher.model.dto.TaskResultDto;
import ru.itmo.textanalyzer.dispatcher.model.enm.TaskStatus;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Nonnull
    private String nameReplacement;

    private Timestamp startTime;
    private Timestamp endTime;

    @Nonnull
    @Lob
    private byte[] file;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<TaskPart> parts;

    @Convert(converter = TaskResultDtoConverter.class)
    private TaskResultDto result;
}
