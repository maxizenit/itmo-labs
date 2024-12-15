package com.vk.itmo.projecttracker.model.entity;

import com.vk.itmo.projecttracker.model.enm.TicketStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"milestone_id", "title"}))
public class Ticket {

    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    TicketStatus status;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "milestone_id", nullable = false)
    Milestone milestone;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
