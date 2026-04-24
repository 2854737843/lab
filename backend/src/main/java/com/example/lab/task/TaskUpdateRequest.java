package com.example.lab.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskUpdateRequest(
    @NotBlank String title,
    String description,
    @NotNull TaskStatus status,
    Long assigneeUserId
) {}
