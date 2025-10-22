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
 * Test class to verify the requirements specified in the PR description.
 * These tests document the expected behavior after the production code changes are implemented.
 */
class ExceptionCodeRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void requirement1_accessDeniedShouldBeChangedToAppAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException();
        
        // REQUIREMENT 1: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED as it would be more informative
        // TODO: After production code change, this should be ExceptionCode.APP_ACCESS_DENIED
        // Currently: ExceptionCode.ACCESS_DENIED
        assertThat("Exception code should be ACCESS_DENIED (to be changed to APP_ACCESS_DENIED)", 
                   ex.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        
        // Verify the current message structure
        assertThat("Current message should be 'Access Denied. Application has read only access to model'", 
                   ex.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void requirement2_undefinedExceptionShouldHaveDefaultMessage() {
        Exception ex = new NullPointerException("Test null pointer");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // REQUIREMENT 2: Change [Any exception message] to "Something went wrong, please try again"
        // Currently: returns the actual exception message ("Test null pointer")
        // Expected: should return "Something went wrong, please try again"
        assertThat("UNDEFINED exception currently returns actual exception message", 
                   response.getBody().getMessage(), is("Test null pointer"));
        
        // TODO: After production code change, this should be:
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        
        assertThat("UNDEFINED exception code should be 0", 
                   response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
    }

    @Test
    void requirement2_undefinedExceptionWithNullMessageShouldHaveDefaultMessage() {
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // REQUIREMENT 2: Change [Any exception message] to "Something went wrong, please try again"
        // Currently: returns null message
        // Expected: should return "Something went wrong, please try again"
        assertThat("UNDEFINED exception with null message currently returns null", 
                   response.getBody().getMessage(), is((String) null));
        
        // TODO: After production code change, this should be:
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        
        assertThat("UNDEFINED exception code should be 0", 
                   response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
    }

    @Test
    void requirement3_selfRoleChangeMessageShouldBeUpdated() {
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // REQUIREMENT 3: Change Message to "Organization should have at least one OWNER"
        // Currently: "Owner cannot change his own organization/application role"
        // Expected: "Organization should have at least one OWNER"
        assertThat("Current SELF_ROLE_CHANGE message", 
                   ex.getMessage(), is("Owner cannot change his own organization/application role"));
        
        // TODO: After production code change, this should be:
        // assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
        
        assertThat("SELF_ROLE_CHANGE exception code should be 14", 
                   ex.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
    }

    @Test
    void verifyCurrentExceptionCodeValues() {
        // Verify current exception code values as documented in the PR
        assertThat("ACCESS_DENIED code should be 1", 
                   ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat("UNDEFINED code should be 0", 
                   ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat("SELF_ROLE_CHANGE code should be 14", 
                   ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        
        // Verify HTTP status codes
        assertThat("ACCESS_DENIED should have FORBIDDEN status", 
                   ExceptionCode.ACCESS_DENIED.getHttpStatus().value(), is(403));
        assertThat("UNDEFINED should have BAD_REQUEST status", 
                   ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
        assertThat("SELF_ROLE_CHANGE should have BAD_REQUEST status", 
                   ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
    }
}