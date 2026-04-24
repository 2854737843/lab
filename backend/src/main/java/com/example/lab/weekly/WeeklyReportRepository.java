package com.example.lab.weekly;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
  List<WeeklyReport> findByAuthorIdOrderBySubmittedAtDesc(Long authorId);
  List<WeeklyReport> findByGroupIdOrderBySubmittedAtDesc(Long groupId);
}
