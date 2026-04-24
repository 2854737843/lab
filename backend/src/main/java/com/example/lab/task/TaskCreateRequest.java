package com.example.lab.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreateRequest(
    @NotNull Long projectId,
    @NotBlank String title,
    String description,
    Long assigneeUserId
) {}
