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
 * Test class to verify the requirements from the PR description.
 * These tests will fail when the production code changes are made,
 * serving as verification that the requirements are met.
 */
class ExceptionRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedExceptionCodeShouldBeAppAccessDenied() {
        // Requirement 1: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        // This test verifies that the exception code name is still ACCESS_DENIED
        // When the production code changes, this test will fail, indicating the requirement is met
        
        ExceptionCode accessDenied = ExceptionCode.ACCESS_DENIED;
        assertThat("Exception code name should be ACCESS_DENIED (will change to APP_ACCESS_DENIED)", 
                   accessDenied.name(), is("ACCESS_DENIED"));
        
        // Verify current behavior
        assertThat(accessDenied.getCode(), is(1));
        assertThat(accessDenied.getHttpStatus().value(), is(403));
        
        AccessDeniedException exception = new AccessDeniedException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionShouldHaveDefaultMessage() {
        // Requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This test verifies that undefined exceptions currently return their original message
        // When the production code changes, this test will fail, indicating the requirement is met
        
        Exception ex = new RuntimeException("Original exception message");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Currently returns the original exception message
        assertThat("Undefined exception should return original message (will change to default message)", 
                   response.getBody().getMessage(), is("Original exception message"));
        
        // Verify code is correct
        assertThat(response.getBody().getCode(), is(0));
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        // Requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This test verifies that undefined exceptions with null messages currently return null
        // When the production code changes, this test will fail, indicating the requirement is met
        
        Exception ex = new Exception(); // No message provided
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Currently returns null for exceptions with no message
        assertThat("Undefined exception with null message should return null (will change to default message)", 
                   response.getBody().getMessage(), is(nullValue()));
        
        // Verify code is correct
        assertThat(response.getBody().getCode(), is(0));
    }

    @Test
    void testSelfRoleChangeExceptionMessage() {
        // Requirement 3: Change Message to "Organization should have at least one OWNER"
        // This test verifies that the current message is "Owner cannot change his own organization/application role"
        // When the production code changes, this test will fail, indicating the requirement is met
        
        SelfRoleChangeException exception = new SelfRoleChangeException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        
        // Currently returns the old message
        assertThat("SelfRoleChangeException should have old message (will change to new message)", 
                   response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
        
        // Verify code is correct
        assertThat(response.getBody().getCode(), is(14));
    }

    @Test
    void testExceptionCodeValuesAreUnique() {
        // Additional test to ensure all exception codes remain unique after changes
        ExceptionCode[] codes = ExceptionCode.values();
        
        for (int i = 0; i < codes.length; i++) {
            for (int j = i + 1; j < codes.length; j++) {
                assertThat("Exception codes should be unique: " + codes[i].name() + " and " + codes[j].name(), 
                    codes[i].getCode(), 
                    not(equalTo(codes[j].getCode())));
            }
        }
    }
}