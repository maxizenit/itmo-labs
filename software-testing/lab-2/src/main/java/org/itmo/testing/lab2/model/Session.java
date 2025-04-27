package org.itmo.testing.lab2.model;

import java.time.LocalDateTime;

public record Session(LocalDateTime loginTime, LocalDateTime logoutTime) {}
