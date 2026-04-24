package com.example.lab.seed;

import com.example.lab.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder) {
    return args -> {
      createIfMissing(users, encoder, "admin", "admin123", Role.ADMIN);
      createIfMissing(users, encoder, "mentor", "mentor123", Role.MENTOR);
      createIfMissing(users, encoder, "student", "student123", Role.STUDENT);
    };
  }

  private void createIfMissing(UserRepository users, PasswordEncoder encoder,
                               String username, String rawPassword, Role role) {
    if (users.existsByUsername(username)) return;
    users.save(User.builder()
        .username(username)
        .passwordHash(encoder.encode(rawPassword))
        .role(role)
        .enabled(true)
        .build());
  }
}
