package com.finance.manager.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void resourceNotFoundException_WithFieldAndValue() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "id", 123L);
        assertEquals("User not found with id: '123'", ex.getMessage());
    }

    @Test
    void resourceNotFoundException_WithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Custom message");
        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    void duplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Category", "name", "Salary");
        assertEquals("Category already exists with name: 'Salary'", ex.getMessage());
    }

    @Test
    void invalidRequestException() {
        InvalidRequestException ex = new InvalidRequestException("Invalid date format");
        assertEquals("Invalid date format", ex.getMessage());
    }

    @Test
    void forbiddenException() {
        ForbiddenException ex = new ForbiddenException("Access denied");
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void unauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Not authenticated");
        assertEquals("Not authenticated", ex.getMessage());
    }

    @Test
    void allExceptions_ExtendRuntimeException() {
        assertTrue(RuntimeException.class.isAssignableFrom(ResourceNotFoundException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(DuplicateResourceException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidRequestException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(ForbiddenException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(UnauthorizedException.class));
    }
}

