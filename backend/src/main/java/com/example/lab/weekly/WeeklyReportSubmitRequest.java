package com.example.lab.weekly;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WeeklyReportSubmitRequest(
    @NotNull Long groupId,
    @NotNull Integer weekYear,
    @NotNull Integer weekNumber,
    @NotBlank String title,
    @NotBlank String content
) {}
