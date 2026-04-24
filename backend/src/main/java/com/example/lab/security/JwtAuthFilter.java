package com.example.lab.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;

  public JwtAuthFilter(JwtService jwt) {
    this.jwt = jwt;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring("Bearer ".length());
      try {
        JwtPrincipal p = jwt.parse(token);
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + p.role()));
        var authentication = new UsernamePasswordAuthenticationToken(p.username(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception ignored) {
        // invalid token -> treat as anonymous
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/auth/") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
  }
}
