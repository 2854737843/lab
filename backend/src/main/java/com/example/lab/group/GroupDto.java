package com.example.lab.group;

public record GroupDto(Long id, String name, String description, boolean archived) {
  public static GroupDto from(Group g) {
    return new GroupDto(g.getId(), g.getName(), g.getDescription(), g.isArchived());
  }
}
