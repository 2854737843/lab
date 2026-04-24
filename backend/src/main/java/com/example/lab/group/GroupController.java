package com.example.lab.group;

import com.example.lab.audit.AuditService;
import com.example.lab.user.*;
import java.time.Instant;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

  private final GroupRepository groups;
  private final GroupMemberRepository members;
  private final UserRepository users;
  private final AuditService audit;

  public GroupController(GroupRepository groups, GroupMemberRepository members, UserRepository users, AuditService audit) {
    this.groups = groups;
    this.members = members;
    this.users = users;
    this.audit = audit;
  }

  // Any authenticated user can list groups they belong to
  @GetMapping("/my")
  public List<GroupDto> myGroups(Authentication auth) {
    User u = users.findByUsername(auth.getName()).orElseThrow();
    return members.findByUserAndActiveTrue(u).stream().map(gm -> GroupDto.from(gm.getGroup())).toList();
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @PostMapping
  public GroupDto create(Authentication auth, @RequestBody @Valid GroupCreateRequest req) {
    Group g = groups.save(Group.builder().name(req.name()).description(req.description()).archived(false).build());

    User creator = users.findByUsername(auth.getName()).orElseThrow();
    members.save(GroupMember.builder()
        .group(g)
        .user(creator)
        .role(GroupRole.OWNER)
        .active(true)
        .joinedAt(Instant.now())
        .build());

    audit.log(auth.getName(), "GROUP", String.valueOf(g.getId()), "CREATE", "name=" + req.name());
    return GroupDto.from(g);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @PutMapping("/{groupId}")
  public GroupDto update(Authentication auth, @PathVariable Long groupId, @RequestBody @Valid GroupUpdateRequest req) {
    Group g = groups.findById(groupId).orElseThrow();
    g.setName(req.name());
    g.setDescription(req.description());
    g.setArchived(req.archived());
    groups.save(g);
    audit.log(auth.getName(), "GROUP", String.valueOf(groupId), "UPDATE", "name=" + req.name());
    return GroupDto.from(g);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @GetMapping("/{groupId}/members")
  public List<GroupMemberDto> listMembers(@PathVariable Long groupId) {
    Group g = groups.findById(groupId).orElseThrow();
    return members.findByGroupAndActiveTrue(g).stream().map(GroupMemberDto::from).toList();
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @PostMapping("/{groupId}/members")
  public ResponseEntity<?> addMember(Authentication auth, @PathVariable Long groupId, @RequestBody @Valid MemberAddRequest req) {
    Group g = groups.findById(groupId).orElseThrow();
    User u = users.findById(req.userId()).orElseThrow();

    var existing = members.findByGroupAndUser(g, u);
    GroupMember gm;
    if (existing.isPresent()) {
      gm = existing.get();
      gm.setRole(req.role());
      gm.setActive(true);
      members.save(gm);
    } else {
      gm = members.save(GroupMember.builder()
          .group(g)
          .user(u)
          .role(req.role())
          .active(true)
          .joinedAt(Instant.now())
          .build());
    }

    audit.log(auth.getName(), "GROUP_MEMBER", String.valueOf(gm.getId()), "ADD", "group=" + groupId + ",user=" + u.getUsername());
    return ResponseEntity.ok(GroupMemberDto.from(gm));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @DeleteMapping("/{groupId}/members/{memberId}")
  public ResponseEntity<?> deactivateMember(Authentication auth, @PathVariable Long groupId, @PathVariable Long memberId) {
    Group g = groups.findById(groupId).orElseThrow();
    GroupMember gm = members.findById(memberId).orElseThrow();
    if (!gm.getGroup().getId().equals(g.getId())) {
      return ResponseEntity.badRequest().body("member not in group");
    }
    gm.setActive(false);
    members.save(gm);
    audit.log(auth.getName(), "GROUP_MEMBER", String.valueOf(memberId), "REMOVE", "group=" + groupId);
    return ResponseEntity.ok().build();
  }
}
