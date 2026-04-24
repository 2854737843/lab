package com.example.lab.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  @Value("${app.jwt.secret:dev-secret-change-me}")
  private String secret;

  @Value("${app.jwt.issuer:lab-system}")
  private String issuer;

  @Value("${app.jwt.expirationMinutes:120}")
  private long expirationMinutes;

  private Key key() {
    // Ensure at least 32 bytes for HS256
    byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
    if (bytes.length < 32) {
      // pad with zeros (dev convenience)
      byte[] padded = new byte[32];
      System.arraycopy(bytes, 0, padded, 0, bytes.length);
      bytes = padded;
    }
    return Keys.hmacShaKeyFor(bytes);
  }

  public String generate(String username, String role) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(expirationMinutes * 60);

    return Jwts.builder()
        .issuer(issuer)
        .subject(username)
        .claim("role", role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key())
        .compact();
  }

  public JwtPrincipal parse(String token) {
    var claims = Jwts.parser()
        .verifyWith((javax.crypto.SecretKey) key())
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    String username = claims.getSubject();
    String role = claims.get("role", String.class);
    return new JwtPrincipal(username, role);
  }
}
