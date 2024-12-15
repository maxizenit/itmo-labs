package com.vk.itmo.projecttracker.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserIdDto(@JsonProperty("userId") String username) {
}
