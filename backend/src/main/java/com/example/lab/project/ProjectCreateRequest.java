package com.example.lab.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectCreateRequest(
    @NotNull Long groupId,
    @NotBlank String name,
    String description
) {}
