package com.vk.itmo.projecttracker.model.mapper;

import com.vk.itmo.projecttracker.model.dto.MilestoneDto;
import com.vk.itmo.projecttracker.model.entity.Milestone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MilestoneMapper {

    @Mapping(source = "id", target = "milestoneId")
    @Mapping(source = "project.id", target = "projectId")
    MilestoneDto fromMilestoneToMilestoneDto(Milestone milestone);
}
