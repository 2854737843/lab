package com.example.lab.weekly;

public record WeeklyReportDto(
    Long id,
    Long groupId,
    Long authorUserId,
    String authorUsername,
    int weekYear,
    int weekNumber,
    String title,
    String content,
    String status,
    String mentorComment,
    Long reviewerUserId,
    String reviewerUsername,
    String submittedAt,
    String reviewedAt
) {
  public static WeeklyReportDto from(WeeklyReport r) {
    return new WeeklyReportDto(
        r.getId(),
        r.getGroup().getId(),
        r.getAuthor().getId(),
        r.getAuthor().getUsername(),
        r.getWeekYear(),
        r.getWeekNumber(),
        r.getTitle(),
        r.getContent(),
        r.getStatus().name(),
        r.getMentorComment(),
        r.getReviewer() == null ? null : r.getReviewer().getId(),
        r.getReviewer() == null ? null : r.getReviewer().getUsername(),
        r.getSubmittedAt() == null ? null : r.getSubmittedAt().toString(),
        r.getReviewedAt() == null ? null : r.getReviewedAt().toString()
    );
  }
}
