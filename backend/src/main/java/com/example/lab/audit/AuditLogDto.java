package com.example.lab.audit;

public record AuditLogDto(Long id, String actor, String entityType, String entityId, String action, String detail, String at) {
  public static AuditLogDto from(AuditLog a) {
    return new AuditLogDto(a.getId(), a.getActor(), a.getEntityType(), a.getEntityId(), a.getAction(), a.getDetail(), a.getAt().toString());
  }
}
