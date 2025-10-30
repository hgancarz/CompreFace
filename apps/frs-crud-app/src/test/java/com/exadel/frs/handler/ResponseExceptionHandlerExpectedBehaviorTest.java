package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class that will FAIL until the PR requirements are implemented.
 * These tests verify the expected behavior after the changes:
 * 1. ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
 * 2. UNDEFINED exception should show "Something went wrong, please try again" instead of actual exception message
 * 3. SELF_ROLE_CHANGE message should be changed to "Organization should have at least one OWNER"
 * 
 * IMPORTANT: These tests are currently expected to FAIL and will start passing
 * once the production code changes are implemented.
 */
class ResponseExceptionHandlerExpectedBehaviorTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedShouldBeRenamedToAppAccessDenied() {
        // This test will FAIL until ACCESS_DENIED is renamed to APP_ACCESS_DENIED
        AccessDeniedException exception = new AccessDeniedException();
        
        // Expected: Exception code name should be APP_ACCESS_DENIED
        // Current: ACCESS_DENIED
        assertThat("ACCESS_DENIED should be renamed to APP_ACCESS_DENIED", 
                   exception.getExceptionCode().name(), is("APP_ACCESS_DENIED"));
    }

    @Test
    void testUndefinedExceptionShouldShowGenericMessage() {
        // This test will FAIL until undefined exceptions show generic message
        Exception exception = new NullPointerException("Test null pointer");
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);
        ExceptionResponseDto responseBody = response.getBody();
        
        // Expected: "Something went wrong, please try again"
        // Current: Actual exception message ("Test null pointer")
        assertThat("UNDEFINED exceptions should show generic message", 
                   responseBody.getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void testUndefinedExceptionWithNullMessageShouldShowGenericMessage() {
        // This test will FAIL until undefined exceptions with null message show generic message
        Exception exception = new NullPointerException();
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);
        ExceptionResponseDto responseBody = response.getBody();
        
        // Expected: "Something went wrong, please try again"
        // Current: null
        assertThat("UNDEFINED exceptions with null message should show generic message", 
                   responseBody.getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void testSelfRoleChangeMessageShouldBeUpdated() {
        // This test will FAIL until SelfRoleChangeException message is updated
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        ExceptionResponseDto responseBody = response.getBody();
        
        // Expected: "Organization should have at least one OWNER"
        // Current: "Owner cannot change his own organization/application role"
        assertThat("SELF_ROLE_CHANGE message should be updated", 
                   responseBody.getMessage(), is("Organization should have at least one OWNER"));
    }

    @Test
    void testExceptionCodeConstantsShouldBeUpdated() {
        // This test will FAIL until ExceptionCode enum is updated
        
        // Expected: APP_ACCESS_DENIED should exist
        // Current: ACCESS_DENIED exists
        try {
            ExceptionCode appAccessDenied = ExceptionCode.valueOf("APP_ACCESS_DENIED");
            // If we get here, APP_ACCESS_DENIED exists
            assertThat("APP_ACCESS_DENIED should have code 1", 
                       appAccessDenied.getCode(), is(1));
            assertThat("APP_ACCESS_DENIED should have FORBIDDEN http status", 
                       appAccessDenied.getHttpStatus(), is(org.springframework.http.HttpStatus.FORBIDDEN));
        } catch (IllegalArgumentException e) {
            // APP_ACCESS_DENIED doesn't exist yet - this is expected until the change is made
            throw new AssertionError("APP_ACCESS_DENIED should exist in ExceptionCode enum");
        }
    }
}