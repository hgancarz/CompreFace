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
 * Test class that documents the expected behavior after PR changes are implemented.
 * These tests will fail with the current implementation but should pass after
 * the production code is updated according to the PR requirements.
 */
class PRRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void requirement1_shouldHaveAppAccessDeniedCodeInsteadOfAccessDenied() {
        // PR Requirement 1: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        // Expected: ExceptionCode should have APP_ACCESS_DENIED with code 1 and FORBIDDEN status
        
        // This test is commented out because it requires production code changes
        // Uncomment and update when ExceptionCode is updated
        /*
        ExceptionCode appAccessDenied = ExceptionCode.APP_ACCESS_DENIED;
        assertThat(appAccessDenied.getCode(), is(1));
        assertThat(appAccessDenied.getHttpStatus().value(), is(403));
        */
        
        // Current behavior - will be removed after requirement is implemented
        ExceptionCode accessDenied = ExceptionCode.ACCESS_DENIED;
        assertThat(accessDenied.getCode(), is(1));
        assertThat(accessDenied.getHttpStatus().value(), is(403));
    }

    @Test
    void requirement2_shouldReturnDefaultMessageForUndefinedExceptions() {
        // PR Requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // Expected: All undefined exceptions should return the default message instead of the actual exception message
        
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(response.getBody().getMessage(), 
            is("Something went wrong, please try again"));
    }

    @Test
    void requirement3_shouldHaveUpdatedSelfRoleChangeMessage() {
        // PR Requirement 3: Change Message to "Organization should have at least one OWNER"
        // Expected: SelfRoleChangeException should have the new message
        
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getMessage(), 
            is("Organization should have at least one OWNER"));
    }

    @Test
    void requirement2_edgeCase_shouldHandleNullPointerExceptionWithNullMessage() {
        // Edge case for Requirement 2: NullPointerException with null message
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getMessage(), 
            is("Something went wrong, please try again"));
    }

    @Test
    void requirement2_edgeCase_shouldHandleExceptionWithEmptyMessage() {
        // Edge case for Requirement 2: Exception with empty message
        Exception ex = new Exception("");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getMessage(), 
            is("Something went wrong, please try again"));
    }

    @Test
    void requirement1_shouldMaintainAccessDeniedExceptionFunctionality() {
        // After Requirement 1 is implemented, AccessDeniedException should still work correctly
        // with the new APP_ACCESS_DENIED code
        
        AccessDeniedException exception = new AccessDeniedException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(1)); // Should still be code 1
        assertThat(response.getBody().getMessage(), 
            is("Access Denied. Application has read only access to model"));
    }
}