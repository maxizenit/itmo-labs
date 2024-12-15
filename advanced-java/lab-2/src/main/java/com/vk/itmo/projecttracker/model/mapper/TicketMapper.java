package com.vk.itmo.projecttracker.model.mapper;

import com.vk.itmo.projecttracker.model.dto.TicketDto;
import com.vk.itmo.projecttracker.model.dto.response.GetUserTicketsResponseDto;
import com.vk.itmo.projecttracker.model.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketMapper {

    @Mapping(source = "id", target = "ticketId")
    @Mapping(source = "milestone.id", target = "milestoneId")
    TicketDto fromTicketToTicketDto(Ticket ticket);

    @Mapping(source = "id", target = "ticketId")
    GetUserTicketsResponseDto fromTicketToGetUserTicketsResponseDto(Ticket ticket);
}
