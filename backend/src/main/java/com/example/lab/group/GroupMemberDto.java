package com.example.lab.group;

public record GroupMemberDto(Long id, Long userId, String username, GroupRole role, boolean active) {
  public static GroupMemberDto from(GroupMember gm) {
    return new GroupMemberDto(
        gm.getId(),
        gm.getUser().getId(),
        gm.getUser().getUsername(),
        gm.getRole(),
        gm.isActive()
    );
  }
}
