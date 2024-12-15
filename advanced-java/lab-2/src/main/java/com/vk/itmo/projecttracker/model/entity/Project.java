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
public class Project {

    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(nullable = false)
    String description;

    @ElementCollection
    @CollectionTable(name = "user_project_role", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "role")
    Map<User, ProjectRole> userProjectRoles;

    @OneToMany(mappedBy = "project")
    Set<BugReport> bugReports;

    @OneToMany(mappedBy = "project")
    Set<Milestone> milestones;
}
