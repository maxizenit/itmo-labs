package com.vk.itmo.projecttracker.controller.v1;

import com.vk.itmo.projecttracker.exception.IncorrectStatusException;
import com.vk.itmo.projecttracker.model.dto.StatusDto;
import com.vk.itmo.projecttracker.model.dto.TicketDto;
import com.vk.itmo.projecttracker.model.dto.request.CreateTicketRequestDto;
import com.vk.itmo.projecttracker.model.enm.MilestoneStatus;
import com.vk.itmo.projecttracker.model.mapper.TicketMapper;
import com.vk.itmo.projecttracker.service.MilestoneService;
import com.vk.itmo.projecttracker.service.TicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/v1/milestones")
public class MilestoneControllerV1 {

    MilestoneService milestoneService;
    TicketService ticketService;
    TicketMapper ticketMapper;

    @PutMapping("/{milestoneId}/status")
    public ResponseEntity<?> updateMilestoneStatus(@PathVariable String milestoneId, @RequestBody StatusDto request) {
        try {
            var milestone = milestoneService.getMilestoneById(milestoneId);
            var status = MilestoneStatus.valueOf(request.status());
            milestoneService.updateMilestoneStatus(milestone, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new IncorrectStatusException();
        }
    }

    @PostMapping("/{milestoneId}/tickets")
    public ResponseEntity<TicketDto> createTicket(@PathVariable String milestoneId,
                                                  @RequestBody CreateTicketRequestDto request) {
        var milestone = milestoneService.getMilestoneById(milestoneId);
        var ticket = ticketService.createTicket(milestone, request.title(), request.description());
        var response = ticketMapper.fromTicketToTicketDto(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
