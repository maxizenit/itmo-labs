package com.vk.itmo.projecttracker.model.mapper;

import com.vk.itmo.projecttracker.model.dto.BugReportDto;
import com.vk.itmo.projecttracker.model.dto.response.GetUserBugReportsDto;
import com.vk.itmo.projecttracker.model.entity.BugReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BugReportMapper {

    @Mapping(source = "id", target = "bugReportId")
    @Mapping(source = "project.id", target = "projectId")
    BugReportDto fromBugReportToBugReportDto(BugReport bugReport);

    @Mapping(source = "id", target = "bugReportId")
    GetUserBugReportsDto fromBugReportToGetUserBugReportsDto(BugReport bugReport);
}
