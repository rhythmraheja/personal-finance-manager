package com.finance.manager.repository;

import com.finance.manager.entity.Goal;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserOrderByCreatedAtDesc(User user);

    Optional<Goal> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);
}

