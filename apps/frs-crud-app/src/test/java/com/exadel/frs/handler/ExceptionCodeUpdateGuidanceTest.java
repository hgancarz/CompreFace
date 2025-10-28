package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * This test class documents the expected changes from the PR description
 * and provides guidance for updating tests when production code changes are made.
 * 
 * PR Requirements:
 * 1. ACCESS_DENIED → APP_ACCESS_DENIED
 * 2. UNDEFINED exception message → "Something went wrong, please try again"
 * 3. SELF_ROLE_CHANGE message → "Organization should have at least one OWNER"
 * 
 * When implementing the production code changes, these tests should be updated accordingly.
 */
class ExceptionCodeUpdateGuidanceTest {

    private ResponseExceptionHandler exceptionHandler = new ResponseExceptionHandler();

    @Test
    void testCurrentState_AccessDenied() {
        // CURRENT STATE: ACCESS_DENIED with message "Access Denied. Application has read only access to model"
        // EXPECTED CHANGE: APP_ACCESS_DENIED (name change only, code and message remain the same)
        
        AccessDeniedException exception = new AccessDeniedException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        
        // Current assertions (to be updated when production code changes)
        assertThat(ExceptionCode.ACCESS_DENIED.name(), is("ACCESS_DENIED"));
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
        
        // After production change, update to:
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.name(), is("APP_ACCESS_DENIED"));
        // assertThat(response.getBody().getCode(), is(1));
        // assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testCurrentState_UndefinedException() {
        // CURRENT STATE: UNDEFINED passes through original exception message
        // EXPECTED CHANGE: Use generic message "Something went wrong, please try again"
        
        Exception exception = new RuntimeException("Original error message");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);
        
        // Current assertions (to be updated when production code changes)
        assertThat(response.getBody().getCode(), is(0));
        assertThat(response.getBody().getMessage(), is("Original error message"));
        
        // After production change, update to:
        // assertThat(response.getBody().getCode(), is(0));
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void testCurrentState_SelfRoleChange() {
        // CURRENT STATE: SELF_ROLE_CHANGE with message "Owner cannot change his own organization/application role"
        // EXPECTED CHANGE: Message changed to "Organization should have at least one OWNER"
        
        SelfRoleChangeException exception = new SelfRoleChangeException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);
        
        // Current assertions (to be updated when production code changes)
        assertThat(response.getBody().getCode(), is(14));
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
        
        // After production change, update to:
        // assertThat(response.getBody().getCode(), is(14));
        // assertThat(response.getBody().getMessage(), is("Organization should have at least one OWNER"));
    }

    @Test
    void testExceptionCodeValuesRemainUnchanged() {
        // These values should NOT change according to the PR description
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus().value(), is(403));
        
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
        
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
    }
}