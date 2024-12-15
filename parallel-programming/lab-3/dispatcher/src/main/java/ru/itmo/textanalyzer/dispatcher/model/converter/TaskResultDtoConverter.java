package ru.itmo.textanalyzer.dispatcher.model.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import ru.itmo.textanalyzer.dispatcher.model.dto.TaskResultDto;

@Converter
public class TaskResultDtoConverter implements AttributeConverter<TaskResultDto, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(TaskResultDto taskResultDto) {
        return OBJECT_MAPPER.writeValueAsString(taskResultDto);
    }

    @SneakyThrows
    @Override
    public TaskResultDto convertToEntityAttribute(String value) {
        return OBJECT_MAPPER.readValue(value, TaskResultDto.class);
    }
}