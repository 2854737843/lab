package com.example.lab.audit;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {
  private final AuditLogRepository repo;

  public AuditService(AuditLogRepository repo) {
    this.repo = repo;
  }

  public void log(String actor, String entityType, String entityId, String action, String detail) {
    repo.save(AuditLog.builder()
        .actor(actor)
        .entityType(entityType)
        .entityId(entityId)
        .action(action)
        .detail(detail)
        .at(Instant.now())
        .build());
  }
}
