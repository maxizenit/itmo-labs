package com.vk.itmo.projecttracker.repository;

import com.vk.itmo.projecttracker.model.entity.Milestone;
import com.vk.itmo.projecttracker.model.entity.Ticket;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TicketRepository extends CrudRepository<Ticket, UUID> {

    boolean existsByMilestoneAndTitle(Milestone milestone, String title);
}
