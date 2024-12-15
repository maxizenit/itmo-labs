package com.vk.itmo.projecttracker.controller.v1;

import com.vk.itmo.projecttracker.exception.IncorrectStatusException;
import com.vk.itmo.projecttracker.exception.UserNotFoundException;
import com.vk.itmo.projecttracker.model.dto.StatusDto;
import com.vk.itmo.projecttracker.model.dto.UserIdDto;
import com.vk.itmo.projecttracker.model.enm.TicketStatus;
import com.vk.itmo.projecttracker.service.TicketService;
import com.vk.itmo.projecttracker.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/v1/tickets")
public class TicketControllerV1 {

    TicketService ticketService;
    UserService userService;

    @PostMapping("/{ticketId}/assign")
    public ResponseEntity<?> assignDeveloperOnTicket(@PathVariable String ticketId, @RequestBody UserIdDto request) {
        var ticket = ticketService.getTicketById(ticketId);
        try {
            var developer = userService.getUserByUsername(request.username());
            ticketService.assignUserOnTicket(ticket, developer);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException _) {
            throw new RuntimeException();
        }
    }

    @GetMapping("/{ticketId}/status")
    public ResponseEntity<StatusDto> getTicketStatus(@PathVariable String ticketId) {
        var ticket = ticketService.getTicketById(ticketId);
        var response = new StatusDto(ticket.getStatus().name());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<StatusDto> updateTicketStatus(@PathVariable String ticketId, @RequestBody StatusDto request) {
        var ticket = ticketService.getTicketById(ticketId);
        try {
            ticketService.updateTicketStatus(ticket, TicketStatus.valueOf(request.status()));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException _) {
            throw new IncorrectStatusException();
        }
    }
}
