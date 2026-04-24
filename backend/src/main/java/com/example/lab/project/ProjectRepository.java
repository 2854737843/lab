package com.example.lab.project;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findByGroupIdAndArchivedFalse(Long groupId);
}
