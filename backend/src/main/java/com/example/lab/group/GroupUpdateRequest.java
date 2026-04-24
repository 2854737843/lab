package com.example.lab.group;

import jakarta.validation.constraints.NotBlank;

public record GroupUpdateRequest(
    @NotBlank String name,
    String description,
    boolean archived
) {}
