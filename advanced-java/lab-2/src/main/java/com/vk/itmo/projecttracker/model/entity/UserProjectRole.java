package com.vk.itmo.projecttracker.model.entity;

import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id", "role"}))
@IdClass(UserProjectRole.UserProjectRoleId.class)
public class UserProjectRole {

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserProjectRoleId {
        User user;
        Project project;
    }

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ProjectRole role;
}
