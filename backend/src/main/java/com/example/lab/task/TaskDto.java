package com.example.lab.task;

public record TaskDto(
    Long id,
    Long projectId,
    String title,
    String description,
    String status,
    Long assigneeUserId,
    String assigneeUsername,
    String createdAt,
    String updatedAt
) {
  public static TaskDto from(Task t) {
    return new TaskDto(
        t.getId(),
        t.getProject().getId(),
        t.getTitle(),
        t.getDescription(),
        t.getStatus().name(),
        t.getAssignee() == null ? null : t.getAssignee().getId(),
        t.getAssignee() == null ? null : t.getAssignee().getUsername(),
        t.getCreatedAt().toString(),
        t.getUpdatedAt().toString()
    );
  }
}
