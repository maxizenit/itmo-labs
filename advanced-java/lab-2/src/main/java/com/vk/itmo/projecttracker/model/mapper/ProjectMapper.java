package com.vk.itmo.projecttracker.model.mapper;

import com.vk.itmo.projecttracker.model.dto.ProjectDto;
import com.vk.itmo.projecttracker.model.dto.response.GetUserProjectsResponseDto;
import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import com.vk.itmo.projecttracker.model.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {

    @Mapping(source = "id", target = "projectId")
    @Mapping(source = "name", target = "projectName")
    ProjectDto fromProjectToProjectDto(Project project);

    @Mapping(source = "entry.key.id", target = "projectId")
    @Mapping(source = "entry.key.name", target = "projectName")
    @Mapping(source = "entry.value", target = "role")
    GetUserProjectsResponseDto fromProjectProjectRoleEntryToGetUserProjectsResponseDto(Map.Entry<Project,
            ProjectRole> entry);
}
