package com.example.lab.weekly;

import com.example.lab.audit.AuditService;
import com.example.lab.group.*;
import com.example.lab.user.*;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weekly-reports")
public class WeeklyReportController {

  private final WeeklyReportRepository reports;
  private final GroupRepository groups;
  private final GroupMemberRepository members;
  private final UserRepository users;
  private final AuditService audit;

  public WeeklyReportController(WeeklyReportRepository reports, GroupRepository groups, GroupMemberRepository members,
                               UserRepository users, AuditService audit) {
    this.reports = reports;
    this.groups = groups;
    this.members = members;
    this.users = users;
    this.audit = audit;
  }

  @GetMapping("/my")
  public List<WeeklyReportDto> my(Authentication auth) {
    User u = users.findByUsername(auth.getName()).orElseThrow();
    return reports.findByAuthorIdOrderBySubmittedAtDesc(u.getId()).stream().map(WeeklyReportDto::from).toList();
  }

  @GetMapping("/by-group/{groupId}")
  public List<WeeklyReportDto> byGroup(Authentication auth, @PathVariable Long groupId) {
    requireActiveMember(auth, groupId);
    return reports.findByGroupIdOrderBySubmittedAtDesc(groupId).stream().map(WeeklyReportDto::from).toList();
  }

  @PostMapping("/submit")
  public WeeklyReportDto submit(Authentication auth, @RequestBody @Valid WeeklyReportSubmitRequest req) {
    requireActiveMember(auth, req.groupId());
    User author = users.findByUsername(auth.getName()).orElseThrow();
    Group g = groups.findById(req.groupId()).orElseThrow();

    WeeklyReport r = WeeklyReport.builder()
        .group(g)
        .author(author)
        .weekYear(req.weekYear())
        .weekNumber(req.weekNumber())
        .title(req.title())
        .content(req.content())
        .status(WeeklyReportStatus.SUBMITTED)
        .submittedAt(Instant.now())
        .build();

    r = reports.save(r);
    audit.log(auth.getName(), "WEEKLY_REPORT", String.valueOf(r.getId()), "SUBMIT", "group=" + g.getId() + ",week=" + req.weekYear() + "-" + req.weekNumber());
    return WeeklyReportDto.from(r);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
  @PostMapping("/{reportId}/review")
  public ResponseEntity<?> review(Authentication auth, @PathVariable Long reportId, @RequestBody @Valid WeeklyReportReviewRequest req) {
    WeeklyReport r = reports.findById(reportId).orElseThrow();
    requireActiveMember(auth, r.getGroup().getId());

    User reviewer = users.findByUsername(auth.getName()).orElseThrow();
    r.setReviewer(reviewer);
    r.setMentorComment(req.mentorComment());
    r.setReviewedAt(Instant.now());
    r.setStatus(req.approve() ? WeeklyReportStatus.APPROVED : WeeklyReportStatus.REJECTED);
    reports.save(r);

    audit.log(auth.getName(), "WEEKLY_REPORT", String.valueOf(reportId), "REVIEW", "status=" + r.getStatus().name());
    return ResponseEntity.ok(WeeklyReportDto.from(r));
  }

  private void requireActiveMember(Authentication auth, Long groupId) {
    Group g = groups.findById(groupId).orElseThrow();
    boolean ok = members.findByGroupAndActiveTrue(g).stream().anyMatch(m -> m.getUser().getUsername().equals(auth.getName()));
    if (!ok) throw new RuntimeException("Not a member of group");
  }
}
