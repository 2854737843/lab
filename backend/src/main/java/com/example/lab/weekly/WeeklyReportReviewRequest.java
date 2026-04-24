package com.example.lab.weekly;

import jakarta.validation.constraints.NotBlank;

public record WeeklyReportReviewRequest(
    boolean approve,
    @NotBlank String mentorComment
) {}
