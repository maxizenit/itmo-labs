package ru.itmo.credithistory.commons.mapper;

import org.jetbrains.annotations.Contract;
import org.mapstruct.MappingTarget;

public interface EntityMapper<E, DTO, CREATE_DTO, UPDATE_DTO> {

  @Contract(value = "null -> null; !null->!null", pure = true)
  DTO fromEntityToDto(E entity);

  @Contract(value = "null -> null; !null->!null", pure = true)
  E fromCreateDtoToEntity(CREATE_DTO createDto);

  void updateEntityFromUpdateDto(@MappingTarget E entity, UPDATE_DTO updateDto);
}
