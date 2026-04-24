package com.example.lab.user;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository users;

  public UserController(UserRepository users) {
    this.users = users;
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @GetMapping
  public List<UserDto> list() {
    return users.findAll().stream().map(UserDto::from).toList();
  }
}
