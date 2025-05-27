package ru.itmo.credithistory.commons.hateoas;

import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

@NullMarked
public abstract class EntityToDtoModelAssembler<E, DTO>
    implements RepresentationModelAssembler<E, EntityModel<DTO>> {

  @Override
  public EntityModel<DTO> toModel(E entity) {
    DTO dto = Objects.requireNonNull(fromEntityToDto(entity));
    EntityModel<DTO> entityModel = EntityModel.of(dto);
    addLinks(entityModel, entity);
    return entityModel;
  }

  protected abstract void addLinks(EntityModel<DTO> entityModel, E entity);

  protected abstract DTO fromEntityToDto(E entity);
}
