package com.example.lab.project;

import com.example.lab.group.Group;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(nullable = false)
  private boolean archived;
}
