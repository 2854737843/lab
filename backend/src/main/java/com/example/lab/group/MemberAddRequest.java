package com.example.lab.group;

import jakarta.validation.constraints.NotNull;

public record MemberAddRequest(
    @NotNull Long userId,
    @NotNull GroupRole role
) {}
