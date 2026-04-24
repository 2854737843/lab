package com.example.lab.audit;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.example.lab.audit.AuditSpecs.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

  private final AuditLogRepository repo;

  public AuditController(AuditLogRepository repo) {
    this.repo = repo;
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @GetMapping
  public List<AuditLogDto> query(
      @RequestParam(required = false) String actor,
      @RequestParam(required = false) String entityType,
      @RequestParam(required = false) String action
  ) {
    var spec = actorLike(actor).and(entityTypeEq(entityType)).and(actionEq(action));
    return repo.findAll(spec, Sort.by(Sort.Direction.DESC, "at")).stream().map(AuditLogDto::from).toList();
  }
}
