package com.vk.itmo.projecttracker.service;

import com.vk.itmo.projecttracker.annotation.CheckProjectRoles;
import com.vk.itmo.projecttracker.exception.AssignNonDeveloperUserToTicketException;
import com.vk.itmo.projecttracker.exception.CreatingTicketInClosedMilestoneException;
import com.vk.itmo.projecttracker.exception.TicketNotFoundException;
import com.vk.itmo.projecttracker.exception.UserAlreadyAssignedToTicketException;
import com.vk.itmo.projecttracker.model.enm.MilestoneStatus;
import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import com.vk.itmo.projecttracker.model.enm.TicketStatus;
import com.vk.itmo.projecttracker.model.entity.Milestone;
import com.vk.itmo.projecttracker.model.entity.Project;
import com.vk.itmo.projecttracker.model.entity.Ticket;
import com.vk.itmo.projecttracker.model.entity.User;
import com.vk.itmo.projecttracker.repository.TicketRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TicketService {

    TicketRepository ticketRepository;

    @Transactional
    @CheckProjectRoles({ProjectRole.manager, ProjectRole.teamleader})
    public synchronized Ticket createTicket(@NonNull Milestone milestone,
                                            @NonNull String title,
                                            @NonNull String description) {
        if (MilestoneStatus.closed.equals(milestone.getStatus())) {
            throw new CreatingTicketInClosedMilestoneException();
        }

        Ticket ticket = new Ticket();
        ticket.setMilestone(milestone);
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setStatus(TicketStatus.open);

        if (ticketRepository.existsByMilestoneAndTitle(milestone, title)) {
            throw new RuntimeException();
        }
        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(@NonNull String ticketId) {
        try {
            UUID uuid = UUID.fromString(ticketId);
            return ticketRepository.findById(uuid).orElseThrow(() -> new TicketNotFoundException(ticketId));
        } catch (IllegalArgumentException _) {
            throw new TicketNotFoundException(ticketId);
        }
    }

    @CheckProjectRoles({ProjectRole.manager, ProjectRole.teamleader})
    public synchronized void assignUserOnTicket(@NonNull Ticket ticket, @NonNull User user) {
        if (ticket.getUser() != null && ticket.getUser().getId().equals(user.getId())) {
            throw new UserAlreadyAssignedToTicketException();
        }

        Project project = ticket.getMilestone().getProject();
        Set<ProjectRole> developerRoles = Set.of(ProjectRole.teamleader, ProjectRole.developer);
        if (!developerRoles.contains(project.getUserProjectRoles().get(user))) {
            throw new AssignNonDeveloperUserToTicketException();
        }
        ticket.setUser(user);
        ticketRepository.save(ticket);
    }

    @CheckProjectRoles({ProjectRole.teamleader, ProjectRole.developer})
    public void updateTicketStatus(@NonNull Ticket ticket, @NonNull TicketStatus newStatus) {
        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);
    }
}
