package com.finance.manager.entity;

import com.finance.manager.enums.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void user_Builder() {
        User user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("password")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("Test User", user.getFullName());
        assertEquals("+1234567890", user.getPhoneNumber());
    }

    @Test
    void user_SettersGetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");
        user.setPhoneNumber("+1234567890");
        user.setCreatedAt(LocalDateTime.now());

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getUsername());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void category_Builder() {
        User user = User.builder().id(1L).build();
        Category category = Category.builder()
                .id(1L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .custom(false)
                .user(user)
                .build();

        assertEquals(1L, category.getId());
        assertEquals("Salary", category.getName());
        assertEquals(TransactionType.INCOME, category.getType());
        assertFalse(category.isCustom());
        assertEquals(user, category.getUser());
    }

    @Test
    void category_SettersGetters() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setType(TransactionType.EXPENSE);
        category.setCustom(true);

        assertEquals(1L, category.getId());
        assertEquals("Food", category.getName());
        assertEquals(TransactionType.EXPENSE, category.getType());
        assertTrue(category.isCustom());
    }

    @Test
    void transaction_Builder() {
        User user = User.builder().id(1L).build();
        
        Transaction transaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date(LocalDate.of(2024, 1, 15))
                .description("Monthly salary")
                .category("Salary")
                .type(TransactionType.INCOME)
                .user(user)
                .build();

        assertEquals(1L, transaction.getId());
        assertEquals(new BigDecimal("5000.00"), transaction.getAmount());
        assertEquals(LocalDate.of(2024, 1, 15), transaction.getDate());
        assertEquals("Monthly salary", transaction.getDescription());
        assertEquals("Salary", transaction.getCategory());
        assertEquals(user, transaction.getUser());
    }

    @Test
    void transaction_SettersGetters() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Test");
        transaction.setCreatedAt(LocalDateTime.now());

        assertEquals(1L, transaction.getId());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertNotNull(transaction.getCreatedAt());
    }

    @Test
    void goal_Builder() {
        User user = User.builder().id(1L).build();
        
        Goal goal = Goal.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .startDate(LocalDate.of(2024, 1, 1))
                .targetDate(LocalDate.of(2025, 1, 1))
                .user(user)
                .build();

        assertEquals(1L, goal.getId());
        assertEquals("Emergency Fund", goal.getGoalName());
        assertEquals(new BigDecimal("10000.00"), goal.getTargetAmount());
        assertEquals(LocalDate.of(2024, 1, 1), goal.getStartDate());
        assertEquals(LocalDate.of(2025, 1, 1), goal.getTargetDate());
        assertEquals(user, goal.getUser());
    }

    @Test
    void goal_SettersGetters() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setGoalName("Vacation");
        goal.setTargetAmount(new BigDecimal("5000.00"));
        goal.setStartDate(LocalDate.now());
        goal.setTargetDate(LocalDate.now().plusYears(1));
        goal.setCreatedAt(LocalDateTime.now());

        assertEquals(1L, goal.getId());
        assertEquals("Vacation", goal.getGoalName());
        assertNotNull(goal.getCreatedAt());
    }

    @Test
    void user_NoArgsConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getUsername());
    }

    @Test
    void category_NoArgsConstructor() {
        Category category = new Category();
        assertNull(category.getId());
        assertNull(category.getName());
    }

    @Test
    void transaction_NoArgsConstructor() {
        Transaction transaction = new Transaction();
        assertNull(transaction.getId());
        assertNull(transaction.getAmount());
    }

    @Test
    void goal_NoArgsConstructor() {
        Goal goal = new Goal();
        assertNull(goal.getId());
        assertNull(goal.getGoalName());
    }
}
