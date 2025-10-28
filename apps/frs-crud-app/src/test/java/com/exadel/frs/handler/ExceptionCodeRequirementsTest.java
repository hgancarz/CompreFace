package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class that verifies the requirements specified in the PR description.
 * These tests document the expected behavior after the production code changes are made.
 * 
 * PR Requirements:
 * 1. Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED as it would be more informative
 * 2. Change [Any exception message] to "Something went wrong, please try again" for UNDEFINED exceptions
 * 3. Change Message to "Organization should have at least one OWNER" for SELF_ROLE_CHANGE
 */
class ExceptionCodeRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void requirement1_accessDeniedShouldBeRenamedToAppAccessDenied() {
        AccessDeniedException exception = new AccessDeniedException();
        
        // After requirement 1 is implemented:
        // - ExceptionCode.ACCESS_DENIED should be renamed to ExceptionCode.APP_ACCESS_DENIED
        // - The exception code value should remain 1
        // - The exception message should remain "Access Denied. Application has read only access to model"
        
        // Current state (before change):
        // assertEquals(ExceptionCode.ACCESS_DENIED, exception.getExceptionCode());
        // assertEquals(1, exception.getExceptionCode().getCode());
        // assertEquals("Access Denied. Application has read only access to model", exception.getMessage());
        
        // Expected state (after change):
        assertEquals(ExceptionCode.APP_ACCESS_DENIED, exception.getExceptionCode());
        assertEquals(1, exception.getExceptionCode().getCode());
        assertEquals("Access Denied. Application has read only access to model", exception.getMessage());
    }

    @Test
    void requirement2_undefinedExceptionsShouldReturnGenericMessage() {
        // Test various undefined exceptions
        
        // After requirement 2 is implemented:
        // - All undefined exceptions should return message "Something went wrong, please try again"
        // - The exception code should remain 0 (UNDEFINED)
        
        // Test with NullPointerException with message
        NullPointerException npeWithMessage = new NullPointerException("Test null pointer");
        ResponseEntity<ExceptionResponseDto> response1 = exceptionHandler.handleUndefinedExceptions(npeWithMessage);
        
        // Current state (before change):
        // assertEquals(ExceptionCode.UNDEFINED.getCode(), response1.getBody().getCode());
        // assertEquals("Test null pointer", response1.getBody().getMessage());
        
        // Expected state (after change):
        assertEquals(ExceptionCode.UNDEFINED.getCode(), response1.getBody().getCode());
        assertEquals("Something went wrong, please try again", response1.getBody().getMessage());
        
        // Test with NullPointerException without message
        NullPointerException npeWithoutMessage = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response2 = exceptionHandler.handleUndefinedExceptions(npeWithoutMessage);
        
        // Current state (before change):
        // assertEquals(ExceptionCode.UNDEFINED.getCode(), response2.getBody().getCode());
        // Note: Currently returns null for message when original exception has null message
        
        // Expected state (after change):
        assertEquals(ExceptionCode.UNDEFINED.getCode(), response2.getBody().getCode());
        assertEquals("Something went wrong, please try again", response2.getBody().getMessage());
        
        // Test with other exception types
        IllegalArgumentException iae = new IllegalArgumentException("Invalid argument");
        ResponseEntity<ExceptionResponseDto> response3 = exceptionHandler.handleUndefinedExceptions(iae);
        
        // Current state (before change):
        // assertEquals(ExceptionCode.UNDEFINED.getCode(), response3.getBody().getCode());
        // assertEquals("Invalid argument", response3.getBody().getMessage());
        
        // Expected state (after change):
        assertEquals(ExceptionCode.UNDEFINED.getCode(), response3.getBody().getCode());
        assertEquals("Something went wrong, please try again", response3.getBody().getMessage());
    }

    @Test
    void requirement3_selfRoleChangeShouldHaveNewMessage() {
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        // After requirement 3 is implemented:
        // - The exception code should remain SELF_ROLE_CHANGE with value 14
        // - The exception message should change to "Organization should have at least one OWNER"
        
        // Current state (before change):
        // assertEquals(ExceptionCode.SELF_ROLE_CHANGE, exception.getExceptionCode());
        // assertEquals(14, exception.getExceptionCode().getCode());
        // assertEquals("Owner cannot change his own organization/application role", exception.getMessage());
        
        // Expected state (after change):
        assertEquals(ExceptionCode.SELF_ROLE_CHANGE, exception.getExceptionCode());
        assertEquals(14, exception.getExceptionCode().getCode());
        assertEquals("Organization should have at least one OWNER", exception.getMessage());
    }

    @Test
    void testExceptionCodeValuesAreCorrect() {
        // Verify that exception codes have the expected values as per PR description
        
        // ACCESS_DENIED (should be APP_ACCESS_DENIED after change) - code 1
        assertEquals(1, ExceptionCode.APP_ACCESS_DENIED.getCode());
        
        // UNDEFINED - code 0
        assertEquals(0, ExceptionCode.UNDEFINED.getCode());
        
        // SELF_ROLE_CHANGE - code 14
        assertEquals(14, ExceptionCode.SELF_ROLE_CHANGE.getCode());
    }
}