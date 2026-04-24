package com.example.lab.audit;

import org.springframework.data.jpa.domain.Specification;

public class AuditSpecs {
  public static Specification<AuditLog> actorLike(String actor) {
    return (root, q, cb) -> actor == null || actor.isBlank() ? cb.conjunction() : cb.like(root.get("actor"), "%" + actor + "%");
  }

  public static Specification<AuditLog> entityTypeEq(String entityType) {
    return (root, q, cb) -> entityType == null || entityType.isBlank() ? cb.conjunction() : cb.equal(root.get("entityType"), entityType);
  }

  public static Specification<AuditLog> actionEq(String action) {
    return (root, q, cb) -> action == null || action.isBlank() ? cb.conjunction() : cb.equal(root.get("action"), action);
  }
}
