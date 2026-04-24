package com.example.lab.project;

public record ProjectDto(Long id, Long groupId, String name, String description, boolean archived) {
  public static ProjectDto from(Project p) {
    return new ProjectDto(p.getId(), p.getGroup().getId(), p.getName(), p.getDescription(), p.isArchived());
  }
}
