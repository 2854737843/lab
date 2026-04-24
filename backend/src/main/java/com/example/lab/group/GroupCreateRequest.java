package com.example.lab.group;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateRequest(
    @NotBlank String name,
    String description
) {}
