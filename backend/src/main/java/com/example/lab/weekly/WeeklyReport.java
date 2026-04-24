package com.example.lab.weekly;

import com.example.lab.group.Group;
import com.example.lab.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(
    name = "weekly_reports",
    uniqueConstraints = @UniqueConstraint(name = "uk_weekly", columnNames = {"group_id", "author_user_id", "weekYear", "weekNumber"})
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "author_user_id", nullable = false)
  private User author;

  // ISO week fields
  @Column(nullable = false)
  private int weekYear;

  @Column(nullable = false)
  private int weekNumber;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 8000)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private WeeklyReportStatus status;

  @Column(length = 2000)
  private String mentorComment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewer_user_id")
  private User reviewer;

  private Instant submittedAt;
  private Instant reviewedAt;
}
