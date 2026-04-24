package com.example.lab.auth;

import com.example.lab.security.JwtService;
import com.example.lab.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final UserRepository users;
  private final JwtService jwt;

  public AuthController(AuthenticationManager authManager, UserRepository users, JwtService jwt) {
    this.authManager = authManager;
    this.users = users;
    this.jwt = jwt;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.username(), req.password()));

    var u = users.findByUsername(req.username()).orElseThrow();
    String token = jwt.generate(u.getUsername(), u.getRole().name());
    return ResponseEntity.ok(new LoginResponse(token, u.getUsername(), u.getRole().name()));
  }
}
