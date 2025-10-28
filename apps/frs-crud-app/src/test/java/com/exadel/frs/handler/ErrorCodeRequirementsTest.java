package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.BasicException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test class to validate the requirements specified in the PR description.
 * This test suite verifies the current behavior and will fail when the production
 * code changes are implemented according to the PR requirements.
 */
class ErrorCodeRequirementsTest {

    private ResponseExceptionHandler exceptionHandler = new ResponseExceptionHandler();

    /**
     * PR Requirement 1:
     * |1|ACCESS_DENIED|FORBIDDEN|Access Denied. Application has read only access to model|
     * Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED as it would be more informative
     */
    @Test
    void testAccessDeniedExceptionCurrentBehavior() {
        // Current behavior - should be updated to APP_ACCESS_DENIED
        AccessDeniedException ex = new AccessDeniedException();
        
        assertThat(ex.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        assertThat(ex.getExceptionCode().getCode(), is(1));
        assertThat(ex.getMessage(), is("Access Denied. Application has read only access to model"));
        
        // Test exception handling
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    /**
     * PR Requirement 2:
     * |0|UNDEFINED|BAD_REQUEST|[Any exception message]|
     * Change [Any exception message] to "Something went wrong, please try again"
     * or discuss with BAs cause now this cause error "null" that is not informative at all.
     */
    @Test
    void testUndefinedExceptionCurrentBehavior() {
        // Current behavior - undefined exceptions pass through the original message
        Exception ex = new NullPointerException();
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(0));
        // Currently returns the original exception message (which could be null)
        // Should be changed to "Something went wrong, please try again"
        assertThat(response.getBody().getMessage(), is(ex.getMessage()));
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        // Test case for null message - currently returns null
        Exception ex = new Exception();
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(0));
        // Currently returns null for exceptions with null message
        // Should be changed to "Something went wrong, please try again"
        assertThat(response.getBody().getMessage(), is(ex.getMessage()));
    }

    /**
     * PR Requirement 3:
     * |15|SELF_ROLE_CHANGE|BAD_REQUEST|Owner cannot change his own organization|
     * Change Message to "Organization should have at least one OWNER"
     * 
     * Note: The PR mentions code 15, but current code is 14. This might need clarification.
     */
    @Test
    void testSelfRoleChangeExceptionCurrentBehavior() {
        // Current behavior - should be updated to new message
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        assertThat(ex.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(ex.getExceptionCode().getCode(), is(14)); // Note: PR says 15, but code is 14
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
        
        // Test exception handling
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(14));
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
    }

    /**
     * Additional test to verify all exception codes are properly handled
     */
    @Test
    void testAllExceptionCodesHaveProperHttpStatus() {
        for (ExceptionCode code : ExceptionCode.values()) {
            assertThat("Exception code " + code.name() + " should have non-null HTTP status", 
                       code.getHttpStatus(), is(notNullValue()));
            assertThat("Exception code " + code.name() + " should have non-null code", 
                       code.getCode(), is(notNullValue()));
        }
    }

    // Helper method for null checks
    private static org.hamcrest.Matcher<Object> notNullValue() {
        return org.hamcrest.CoreMatchers.notNullValue();
    }
}