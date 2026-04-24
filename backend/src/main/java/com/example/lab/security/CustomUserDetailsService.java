package com.example.lab.security;

import com.example.lab.user.User;
import com.example.lab.user.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository users;

  public CustomUserDetailsService(UserRepository users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User u = users.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return org.springframework.security.core.userdetails.User
        .withUsername(u.getUsername())
        .password(u.getPasswordHash())
        .disabled(!u.isEnabled())
        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())))
        .build();
  }
}
