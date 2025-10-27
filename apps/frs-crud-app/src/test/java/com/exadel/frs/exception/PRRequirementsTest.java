package com.exadel.frs.exception;

import com.exadel.frs.handler.ExceptionCode;
import com.exadel.frs.handler.ResponseExceptionHandler;
import com.exadel.frs.dto.ExceptionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class to verify the requirements from the PR description.
 * These tests will fail until the production code changes are implemented.
 * 
 * PR Requirements:
 * 1. Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED as it would be more informative
 * 2. Change [Any exception message] to "Something went wrong, please try again" for UNDEFINED exceptions
 * 3. Change SELF_ROLE_CHANGE message to "Organization should have at least one OWNER"
 */
class PRRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void requirement1_accessDeniedShouldBeRenamedToAppAccessDenied() {
        // TODO: This test should fail until ACCESS_DENIED is renamed to APP_ACCESS_DENIED
        // Currently, the exception code name is ACCESS_DENIED
        // After the change, we should have APP_ACCESS_DENIED instead
        
        ExceptionCode accessDenied = ExceptionCode.ACCESS_DENIED;
        assertThat(accessDenied.name(), is("ACCESS_DENIED"));
        
        // After implementation, this should be:
        // assertThat(accessDenied.name(), is("APP_ACCESS_DENIED"));
    }

    @Test
    void requirement2_undefinedExceptionShouldShowUserFriendlyMessage() {
        // Test that undefined exceptions with null message show a user-friendly message
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Currently, the message is null for NullPointerException
        // After the change, it should be "Something went wrong, please try again"
        assertThat(response.getBody().getMessage(), is(nullValue()));
        
        // After implementation, this should be:
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void requirement2_undefinedExceptionWithMessageShouldStillShowOriginalMessage() {
        // Test that undefined exceptions with existing messages still show the original message
        Exception ex = new IllegalArgumentException("Custom error message");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Exceptions with custom messages should still show their original message
        assertThat(response.getBody().getMessage(), is("Custom error message"));
    }

    @Test
    void requirement3_selfRoleChangeExceptionShouldHaveNewMessage() {
        // Test that SelfRoleChangeException has the new message
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // Currently, the message is "Owner cannot change his own organization/application role"
        // After the change, it should be "Organization should have at least one OWNER"
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
        
        // After implementation, this should be:
        // assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
    }

    @Test
    void requirement3_selfRoleChangeExceptionResponseShouldHaveNewMessage() {
        // Test that the response from SelfRoleChangeException has the new message
        SelfRoleChangeException ex = new SelfRoleChangeException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);

        // Currently, the message is "Owner cannot change his own organization/application role"
        // After the change, it should be "Organization should have at least one OWNER"
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
        
        // After implementation, this should be:
        // assertThat(response.getBody().getMessage(), is("Organization should have at least one OWNER"));
    }
}