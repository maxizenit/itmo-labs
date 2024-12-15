package ru.itmo.textanalyzer.dispatcher.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.itmo.textanalyzer.commons.dto.TaskPartDto;
import ru.itmo.textanalyzer.dispatcher.model.entity.TaskPart;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskPartMapper {

    @Mapping(source = "task.nameReplacement", target = "nameReplacement")
    TaskPartDto fromTaskPartToTaskPartDto(TaskPart taskPart);
}
