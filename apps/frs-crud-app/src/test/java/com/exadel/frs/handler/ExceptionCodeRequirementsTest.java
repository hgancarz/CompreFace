package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test class that verifies the requirements specified in the PR description.
 * These tests are designed to fail with the current implementation but pass after
 * the production code changes are made according to the PR requirements.
 */
class ExceptionCodeRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedCodeShouldBeAppAccessDenied() {
        // Requirement: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED as it would be more informative
        // This test verifies that the enum constant name should be APP_ACCESS_DENIED
        
        // Currently this will fail because the enum constant is named ACCESS_DENIED
        // After the change, this test should pass
        boolean hasAppAccessDenied = false;
        for (ExceptionCode code : ExceptionCode.values()) {
            if ("APP_ACCESS_DENIED".equals(code.name())) {
                hasAppAccessDenied = true;
                break;
            }
        }
        
        assertThat("ExceptionCode should have APP_ACCESS_DENIED constant", hasAppAccessDenied, is(true));
    }

    @Test
    void testUndefinedExceptionShouldReturnGenericMessage() {
        // Requirement: Change [Any exception message] to "Something went wrong, please try again"
        // This test verifies that UNDEFINED exceptions return a generic message
        
        Exception originalException = new NullPointerException("null");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(originalException);
        
        // Currently this returns the original exception message
        // After the change, this should return the generic message
        String expectedMessage = "Something went wrong, please try again";
        assertThat("UNDEFINED exception should return generic message", 
                   response.getBody().getMessage(), is(expectedMessage));
    }

    @Test
    void testSelfRoleChangeExceptionShouldHaveUpdatedMessage() {
        // Requirement: Change Message to "Organization should have at least one OWNER"
        // This test verifies that SelfRoleChangeException has the updated message
        
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        // Currently this returns the old message
        // After the change, this should return the new message
        String expectedMessage = "Organization should have at least one OWNER";
        assertThat("SelfRoleChangeException should have updated message", 
                   exception.getMessage(), is(expectedMessage));
    }

    @Test
    void testExceptionCodeValuesAreCorrect() {
        // Verify that the exception codes have the correct numeric values and HTTP statuses
        
        // Test ACCESS_DENIED (will be APP_ACCESS_DENIED after change)
        ExceptionCode accessDeniedCode = ExceptionCode.ACCESS_DENIED;
        assertThat(accessDeniedCode.getCode(), is(1));
        assertThat(accessDeniedCode.getHttpStatus().value(), is(403));
        
        // Test UNDEFINED
        ExceptionCode undefinedCode = ExceptionCode.UNDEFINED;
        assertThat(undefinedCode.getCode(), is(0));
        assertThat(undefinedCode.getHttpStatus().value(), is(400));
        
        // Test SELF_ROLE_CHANGE
        ExceptionCode selfRoleChangeCode = ExceptionCode.SELF_ROLE_CHANGE;
        assertThat(selfRoleChangeCode.getCode(), is(14));
        assertThat(selfRoleChangeCode.getHttpStatus().value(), is(400));
    }

    @Test
    void testAccessDeniedExceptionMessageIsCorrect() {
        // Verify that AccessDeniedException has the correct message
        AccessDeniedException exception = new AccessDeniedException();
        
        String expectedMessage = "Access Denied. Application has read only access to model";
        assertThat(exception.getMessage(), is(expectedMessage));
    }
}