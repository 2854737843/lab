package com.example.lab.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectUpdateRequest(
    @NotBlank String name,
    String description,
    boolean archived
) {}
