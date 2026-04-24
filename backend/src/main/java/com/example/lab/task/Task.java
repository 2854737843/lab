package com.example.lab.task;

import com.example.lab.project.Project;
import com.example.lab.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "tasks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(length = 4000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private TaskStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_user_id")
  private User assignee;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;
}
