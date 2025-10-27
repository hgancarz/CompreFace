package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test class that verifies the PR requirements for error codes and messages.
 * These tests are designed to fail initially and pass after the production code changes.
 */
class ExceptionCodeRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedCodeNameShouldBeAppAccessDenied() {
        // PR Requirement 1: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        // This test will fail until the production code is changed
        assertThat("ACCESS_DENIED code name should be changed to APP_ACCESS_DENIED", 
                   ExceptionCode.ACCESS_DENIED.name(), 
                   is(equalTo("APP_ACCESS_DENIED")));
    }

    @Test
    void testUndefinedExceptionShouldShowGenericMessage() {
        // PR Requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This test verifies that undefined exceptions show a generic message instead of null
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.UNDEFINED.getCode())));
        
        // Current behavior: shows null for NullPointerException
        // Expected behavior after PR: should show "Something went wrong, please try again"
        assertThat("Undefined exceptions should show generic message instead of null",
                   response.getBody().getMessage(), 
                   is(equalTo("Something went wrong, please try again")));
    }

    @Test
    void testSelfRoleChangeExceptionShouldHaveNewMessage() {
        // PR Requirement 3: Change Message to "Organization should have at least one OWNER"
        // This test will fail until the production code is changed
        SelfRoleChangeException ex = new SelfRoleChangeException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);

        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.SELF_ROLE_CHANGE.getCode())));
        
        // Current message: "Owner cannot change his own organization/application role"
        // Expected message after PR: "Organization should have at least one OWNER"
        assertThat("SelfRoleChangeException message should be updated",
                   response.getBody().getMessage(), 
                   is(equalTo("Organization should have at least one OWNER")));
    }

    @Test
    void testUndefinedExceptionWithActualMessageShouldShowGenericMessage() {
        // Additional test for Requirement 2: Even when exception has a message, should show generic one
        Exception ex = new IllegalArgumentException("Specific error details");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.UNDEFINED.getCode())));
        
        // Current behavior: shows "Specific error details"
        // Expected behavior after PR: should show "Something went wrong, please try again"
        assertThat("Undefined exceptions should always show generic message",
                   response.getBody().getMessage(), 
                   is(equalTo("Something went wrong, please try again")));
    }
}