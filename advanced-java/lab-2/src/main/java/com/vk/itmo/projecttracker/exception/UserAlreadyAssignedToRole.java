package com.vk.itmo.projecttracker.exception;

import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UserAlreadyAssignedToRole extends BadRequestException {

    static String MESSAGE = "User already assigned to role \"%s\"";

    public UserAlreadyAssignedToRole(ProjectRole role) {
        super(MESSAGE.formatted(role.name()));
    }
}
