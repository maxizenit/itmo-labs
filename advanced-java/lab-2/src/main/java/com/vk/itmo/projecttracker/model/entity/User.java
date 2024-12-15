package com.vk.itmo.projecttracker.model.entity;

import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_")
public class User {

    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    char[] password;

    @Column(nullable = false)
    String email;

    @ElementCollection
    @CollectionTable(name = "user_project_role", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyJoinColumn(name = "project_id")
    @Column(name = "role")
    Map<Project, ProjectRole> userProjectRoles;

    @OneToMany(mappedBy = "user")
    Set<Ticket> tickets;

    @OneToMany(mappedBy = "user")
    Set<BugReport> bugReports;
}
