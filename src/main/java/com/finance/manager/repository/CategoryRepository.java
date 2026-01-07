package com.finance.manager.repository;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user = :user OR c.user IS NULL")
    List<Category> findAllByUserOrDefault(@Param("user") User user);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND (c.user = :user OR c.user IS NULL)")
    Optional<Category> findByNameAndUser(@Param("name") String name, @Param("user") User user);

    boolean existsByNameAndUser(String name, User user);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c " +
           "WHERE c.name = :name AND (c.user = :user OR c.user IS NULL)")
    boolean existsByNameForUser(@Param("name") String name, @Param("user") User user);

    List<Category> findByUserIsNull();

    List<Category> findByUser(User user);

    Optional<Category> findByNameAndUserIsNull(String name);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.type = :type AND c.user IS NULL")
    Optional<Category> findDefaultByNameAndType(@Param("name") String name, @Param("type") TransactionType type);
}

