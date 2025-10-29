package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Comprehensive test class that verifies all PR requirements.
 * This test should be updated once the production code changes are implemented.
 */
class PRRequirementsVerificationTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void verifyAllPRRequirements() {
        // Requirement 1: ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        verifyRequirement1();
        
        // Requirement 2: UNDEFINED exceptions should return generic message
        verifyRequirement2();
        
        // Requirement 3: SELF_ROLE_CHANGE message should be updated
        verifyRequirement3();
    }

    private void verifyRequirement1() {
        // Current state - APP_ACCESS_DENIED exists
        assertThat(ExceptionCode.APP_ACCESS_DENIED.name(), is("APP_ACCESS_DENIED"));
        assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.APP_ACCESS_DENIED.getHttpStatus(), is(FORBIDDEN));
        
        // After implementation:
        // - ExceptionCode.ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        // - All references should be updated
        // - The code (1) and HTTP status (FORBIDDEN) should remain the same
        // - The message in AccessDeniedException should remain "Access Denied. Application has read only access to model"
        
        AccessDeniedException accessDeniedEx = new AccessDeniedException();
        assertThat(accessDeniedEx.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    private void verifyRequirement2() {
        // Current behavior: undefined exceptions return their actual message
        Exception nullPointerEx = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response1 = exceptionHandler.handleUndefinedExceptions(nullPointerEx);
        assertThat(response1.getBody().getMessage(), is("Something went wrong, please try again"));
        
        Exception illegalArgEx = new IllegalArgumentException("Test message");
        ResponseEntity<ExceptionResponseDto> response2 = exceptionHandler.handleUndefinedExceptions(illegalArgEx);
        assertThat(response2.getBody().getMessage(), is("Something went wrong, please try again"));
        
        // After implementation:
        // - ALL undefined exceptions should return: "Something went wrong, please try again"
        // - The ResponseExceptionHandler.handleUndefinedExceptions method should be modified
        // - The code (0) and HTTP status (BAD_REQUEST) should remain the same for UNDEFINED
        
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(BAD_REQUEST));
    }

    private void verifyRequirement3() {
        // Current message
        SelfRoleChangeException selfRoleChangeEx = new SelfRoleChangeException();
        assertThat(selfRoleChangeEx.getMessage(), is("Organization should have at least one OWNER"));
        
        // After implementation:
        // - The message should be changed to: "Organization should have at least one OWNER"
        // - The SelfRoleChangeException.MESSAGE constant should be updated
        // - The code (14) and HTTP status (BAD_REQUEST) should remain the same
        
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus(), is(BAD_REQUEST));
    }

    @Test
    void verifyExceptionResponseStructure() {
        // Verify that the exception response structure remains consistent
        AccessDeniedException ex = new AccessDeniedException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        
        assertThat(response.getBody().getCode(), is(ex.getExceptionCode().getCode()));
        assertThat(response.getBody().getMessage(), is(ex.getMessage()));
        assertThat(response.getStatusCode(), is(ex.getExceptionCode().getHttpStatus()));
    }
}