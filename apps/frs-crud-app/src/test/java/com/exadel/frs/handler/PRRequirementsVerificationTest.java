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
 * This test class documents and verifies the PR requirements.
 * 
 * PR Requirements:
 * 1. Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED as it would be more informative
 * 2. Change [Any exception message] to "Something went wrong, please try again" 
 *    (for UNDEFINED exceptions)
 * 3. Change Message to "Organization should have at least one OWNER" 
 *    (for SELF_ROLE_CHANGE exception)
 * 
 * These tests are designed to FAIL with the current implementation 
 * and PASS after the production code changes are made.
 */
class PRRequirementsVerificationTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void requirement1_accessDeniedShouldBeRenamedToAppAccessDenied() {
        // PR Requirement 1: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        
        // Current state: ExceptionCode.ACCESS_DENIED exists
        // Expected state: ExceptionCode.APP_ACCESS_DENIED should exist
        
        boolean hasAppAccessDenied = false;
        for (ExceptionCode code : ExceptionCode.values()) {
            if ("APP_ACCESS_DENIED".equals(code.name())) {
                hasAppAccessDenied = true;
                break;
            }
        }
        
        assertThat("ExceptionCode should have APP_ACCESS_DENIED constant (not ACCESS_DENIED)", 
                   hasAppAccessDenied, is(true));
    }

    @Test
    void requirement2_undefinedExceptionsShouldReturnGenericMessage() {
        // PR Requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        
        // Current state: UNDEFINED exceptions return the original exception message
        // Expected state: UNDEFINED exceptions should return generic message
        
        // Test with NullPointerException
        Exception nullPointerException = new NullPointerException("null");
        ResponseEntity<ExceptionResponseDto> response1 = exceptionHandler.handleUndefinedExceptions(nullPointerException);
        
        String expectedGenericMessage = "Something went wrong, please try again";
        assertThat("UNDEFINED exception with NullPointerException should return generic message", 
                   response1.getBody().getMessage(), is(expectedGenericMessage));
        
        // Test with other exception types
        Exception illegalArgumentException = new IllegalArgumentException("Invalid argument");
        ResponseEntity<ExceptionResponseDto> response2 = exceptionHandler.handleUndefinedExceptions(illegalArgumentException);
        
        assertThat("UNDEFINED exception with IllegalArgumentException should return generic message", 
                   response2.getBody().getMessage(), is(expectedGenericMessage));
    }

    @Test
    void requirement3_selfRoleChangeExceptionShouldHaveUpdatedMessage() {
        // PR Requirement 3: Change Message to "Organization should have at least one OWNER"
        
        // Current state: SelfRoleChangeException message is "Owner cannot change his own organization/application role"
        // Expected state: SelfRoleChangeException message should be "Organization should have at least one OWNER"
        
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        String expectedMessage = "Organization should have at least one OWNER";
        assertThat("SelfRoleChangeException should have updated message", 
                   exception.getMessage(), is(expectedMessage));
    }

    @Test
    void requirement1_verifyAccessDeniedExceptionStillWorks() {
        // Even after renaming ACCESS_DENIED to APP_ACCESS_DENIED, 
        // AccessDeniedException should still work correctly
        
        AccessDeniedException exception = new AccessDeniedException();
        
        // The exception code should be APP_ACCESS_DENIED (after change)
        // The numeric code should remain 1
        // The HTTP status should remain FORBIDDEN (403)
        // The message should remain the same
        
        assertThat("AccessDeniedException code should be 1", 
                   exception.getExceptionCode().getCode(), is(1));
        assertThat("AccessDeniedException HTTP status should be 403", 
                   exception.getExceptionCode().getHttpStatus().value(), is(403));
        assertThat("AccessDeniedException message should be correct", 
                   exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void requirement2_verifyUndefinedExceptionCodeRemainsSame() {
        // The UNDEFINED exception code should remain the same
        
        assertThat("UNDEFINED code should be 0", 
                   ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat("UNDEFINED HTTP status should be 400", 
                   ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
    }

    @Test
    void requirement3_verifySelfRoleChangeExceptionCodeRemainsSame() {
        // The SELF_ROLE_CHANGE exception code should remain the same
        
        assertThat("SELF_ROLE_CHANGE code should be 14", 
                   ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat("SELF_ROLE_CHANGE HTTP status should be 400", 
                   ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
    }
}