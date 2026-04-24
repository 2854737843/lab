package com.example.lab.user;

public record UserDto(Long id, String username, String role, boolean enabled) {
  public static UserDto from(User u) {
    return new UserDto(u.getId(), u.getUsername(), u.getRole().name(), u.isEnabled());
  }
}
