package com.example.lab.audit;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 64)
  private String actor;

  @Column(nullable = false, length = 40)
  private String entityType;

  @Column(nullable = false, length = 64)
  private String entityId;

  @Column(nullable = false, length = 40)
  private String action;

  @Column(length = 2000)
  private String detail;

  @Column(nullable = false)
  private Instant at;
}
