package com.example.lab.task;

import com.example.lab.audit.AuditService;
import com.example.lab.group.*;
import com.example.lab.project.*;
import com.example.lab.user.*;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskRepository tasks;
  private final ProjectRepository projects;
  private final GroupRepository groups;
  private final GroupMemberRepository members;
  private final UserRepository users;
  private final AuditService audit;

  public TaskController(TaskRepository tasks, ProjectRepository projects, GroupRepository groups,
                        GroupMemberRepository members, UserRepository users, AuditService audit) {
    this.tasks = tasks;
    this.projects = projects;
    this.groups = groups;
    this.members = members;
    this.users = users;
    this.audit = audit;
  }

  @GetMapping("/by-project/{projectId}")
  public List<TaskDto> list(Authentication auth, @PathVariable Long projectId) {
    Project p = projects.findById(projectId).orElseThrow();
    requireActiveMember(auth, p.getGroup().getId());
    return tasks.findByProjectIdOrderByUpdatedAtDesc(projectId).stream().map(TaskDto::from).toList();
  }

  @PostMapping
  public TaskDto create(Authentication auth, @RequestBody @Valid TaskCreateRequest req) {
    Project p = projects.findById(req.projectId()).orElseThrow();
    requireActiveMember(auth, p.getGroup().getId());

    User assignee = null;
    if (req.assigneeUserId() != null) {
      assignee = users.findById(req.assigneeUserId()).orElseThrow();
    }

    Instant now = Instant.now();
    Task t = tasks.save(Task.builder()
        .project(p)
        .title(req.title())
        .description(req.description())
        .status(TaskStatus.TODO)
        .assignee(assignee)
        .createdAt(now)
        .updatedAt(now)
        .build());

    audit.log(auth.getName(), "TASK", String.valueOf(t.getId()), "CREATE", "project=" + p.getId() + ",title=" + t.getTitle());
    return TaskDto.from(t);
  }

  @PutMapping("/{taskId}")
  public TaskDto update(Authentication auth, @PathVariable Long taskId, @RequestBody @Valid TaskUpdateRequest req) {
    Task t = tasks.findById(taskId).orElseThrow();
    requireActiveMember(auth, t.getProject().getGroup().getId());

    User assignee = null;
    if (req.assigneeUserId() != null) {
      assignee = users.findById(req.assigneeUserId()).orElseThrow();
    }

    t.setTitle(req.title());
    t.setDescription(req.description());
    t.setStatus(req.status());
    t.setAssignee(assignee);
    t.setUpdatedAt(Instant.now());
    tasks.save(t);

    audit.log(auth.getName(), "TASK", String.valueOf(taskId), "UPDATE", "status=" + req.status().name());
    return TaskDto.from(t);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @DeleteMapping("/{taskId}")
  public void delete(Authentication auth, @PathVariable Long taskId) {
    Task t = tasks.findById(taskId).orElseThrow();
    requireActiveMember(auth, t.getProject().getGroup().getId());
    tasks.delete(t);
    audit.log(auth.getName(), "TASK", String.valueOf(taskId), "DELETE", "");
  }

  private void requireActiveMember(Authentication auth, Long groupId) {
    Group g = groups.findById(groupId).orElseThrow();
    boolean ok = members.findByGroupAndActiveTrue(g).stream().anyMatch(m -> m.getUser().getUsername().equals(auth.getName()));
    if (!ok) throw new RuntimeException("Not a member of group");
  }
}
