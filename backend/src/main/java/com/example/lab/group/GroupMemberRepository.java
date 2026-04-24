package com.example.lab.group;

import com.example.lab.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
  List<GroupMember> findByUserAndActiveTrue(User user);
  List<GroupMember> findByGroupAndActiveTrue(Group group);
  Optional<GroupMember> findByGroupAndUser(Group group, User user);
  boolean existsByGroupAndUser(Group group, User user);
}
