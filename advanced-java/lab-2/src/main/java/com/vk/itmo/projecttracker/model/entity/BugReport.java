package com.vk.itmo.projecttracker.model.entity;

import com.vk.itmo.projecttracker.model.enm.BugReportStatus;
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
public class BugReport {

    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    BugReportStatus status;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
