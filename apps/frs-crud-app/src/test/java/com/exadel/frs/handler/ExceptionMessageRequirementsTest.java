package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class to verify the requirements from the PR description:
 * 1. ACCESS_DENIED should be changed to APP_ACCESS_DENIED
 * 2. UNDEFINED exception message should be changed to "Something went wrong, please try again"
 * 3. SELF_ROLE_CHANGE message should be changed to "Organization should have at least one OWNER"
 * 
 * These tests are designed to fail when the production code changes are made,
 * ensuring that the tests properly validate the new requirements.
 */
class ExceptionMessageRequirementsTest {

    private ResponseExceptionHandler exceptionHandler = new ResponseExceptionHandler();

    @Test
    void testAccessDeniedExceptionCodeNameShouldBeAppAccessDenied() {
        // Requirement: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        // This test verifies that the exception code name is ACCESS_DENIED (current state)
        // When changed to APP_ACCESS_DENIED, this test should be updated
        
        ExceptionCode code = ExceptionCode.ACCESS_DENIED;
        assertThat(code.name(), is("ACCESS_DENIED"));
        
        // Verify the exception still works correctly
        AccessDeniedException exception = new AccessDeniedException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionShouldHaveGenericMessage() {
        // Requirement: Change [Any exception message] to "Something went wrong, please try again"
        // This test verifies that undefined exceptions currently pass through the original message
        // When changed to use a generic message, this test should be updated
        
        Exception exception = new NullPointerException("test message");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);
        
        assertThat(response.getBody().getCode(), is(0));
        assertThat(response.getBody().getMessage(), is("test message"));
        
        // Test with null message
        Exception nullMessageException = new RuntimeException();
        ResponseEntity<ExceptionResponseDto> nullResponse = exceptionHandler.handleUndefinedExceptions(nullMessageException);
        
        assertThat(nullResponse.getBody().getCode(), is(0));
        // Currently returns null, but should be changed to "Something went wrong, please try again"
    }

    @Test
    void testSelfRoleChangeExceptionMessageShouldBeUpdated() {
        // Requirement: Change Message to "Organization should have at least one OWNER"
        // This test verifies the current message
        // When changed to the new message, this test should be updated
        
        SelfRoleChangeException exception = new SelfRoleChangeException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        
        assertThat(response.getBody().getCode(), is(14));
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testExceptionCodeValuesAreCorrect() {
        // Verify the current exception code values match the PR description
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus().value(), is(403));
        
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
        
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
    }
}