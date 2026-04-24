package com.example.lab.project;

import com.example.lab.audit.AuditService;
import com.example.lab.group.*;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectRepository projects;
  private final GroupRepository groups;
  private final GroupMemberRepository members;
  private final AuditService audit;

  public ProjectController(ProjectRepository projects, GroupRepository groups, GroupMemberRepository members, AuditService audit) {
    this.projects = projects;
    this.groups = groups;
    this.members = members;
    this.audit = audit;
  }

  @GetMapping("/by-group/{groupId}")
  public List<ProjectDto> listByGroup(Authentication auth, @PathVariable Long groupId) {
    // Basic access check: must be an active member of group
    requireActiveMember(auth, groupId);
    return projects.findByGroupIdAndArchivedFalse(groupId).stream().map(ProjectDto::from).toList();
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @PostMapping
  public ProjectDto create(Authentication auth, @RequestBody @Valid ProjectCreateRequest req) {
    requireActiveMember(auth, req.groupId());
    Group g = groups.findById(req.groupId()).orElseThrow();
    Project p = projects.save(Project.builder().group(g).name(req.name()).description(req.description()).archived(false).build());
    audit.log(auth.getName(), "PROJECT", String.valueOf(p.getId()), "CREATE", "group=" + g.getId() + ",name=" + p.getName());
    return ProjectDto.from(p);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @PutMapping("/{projectId}")
  public ProjectDto update(Authentication auth, @PathVariable Long projectId, @RequestBody @Valid ProjectUpdateRequest req) {
    Project p = projects.findById(projectId).orElseThrow();
    requireActiveMember(auth, p.getGroup().getId());
    p.setName(req.name());
    p.setDescription(req.description());
    p.setArchived(req.archived());
    projects.save(p);
    audit.log(auth.getName(), "PROJECT", String.valueOf(projectId), "UPDATE", "name=" + req.name());
    return ProjectDto.from(p);
  }

  private void requireActiveMember(Authentication auth, Long groupId) {
    Group g = groups.findById(groupId).orElseThrow();
    var userMember = members.findByGroupAndActiveTrue(g).stream()
        .filter(m -> m.getUser().getUsername().equals(auth.getName()))
        .findFirst();
    if (userMember.isEmpty()) {
      throw new RuntimeException("Not a member of group");
    }
  }
}
