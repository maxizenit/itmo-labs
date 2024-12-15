package ru.itmo.textanalyzer.dispatcher.model.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import ru.itmo.textanalyzer.commons.dto.TaskPartResultDto;

@Converter
public class TaskPartResultDtoConverter implements AttributeConverter<TaskPartResultDto, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(TaskPartResultDto taskPartResultDto) {
        return OBJECT_MAPPER.writeValueAsString(taskPartResultDto);
    }

    @SneakyThrows
    @Override
    public TaskPartResultDto convertToEntityAttribute(String value) {
        return OBJECT_MAPPER.readValue(value, TaskPartResultDto.class);
    }
}
