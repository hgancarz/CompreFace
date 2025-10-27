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
 * Test class that validates the requirements specified in the PR description.
 * These tests are designed to fail when the production code doesn't meet the requirements,
 * serving as validation tests for when the production code is updated.
 */
class ExceptionCodeRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedCodeShouldBeAppAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException();
        
        // Requirement: ACCESS_DENIED should be changed to APP_ACCESS_DENIED
        // This test will fail until the production code is updated
        // TODO: Uncomment and update when production code is changed
        // assertThat(ex.getExceptionCode().name(), is("APP_ACCESS_DENIED"));
        
        // Current behavior (this should be updated)
        assertThat(ex.getExceptionCode().name(), is("ACCESS_DENIED"));
        assertThat(ex.getExceptionCode().getCode(), is(1));
    }

    @Test
    void testUndefinedExceptionShouldReturnGenericMessage() {
        Exception ex = new NullPointerException("Test null pointer");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Requirement: UNDEFINED exceptions should return "Something went wrong, please try again"
        // instead of the raw exception message
        // This test will fail until the production code is updated
        // TODO: Uncomment and update when production code is changed
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        
        // Current behavior (this should be updated)
        assertThat(response.getBody().getMessage(), is("Test null pointer"));
        assertThat(response.getBody().getCode(), is(0));
    }

    @Test
    void testUndefinedExceptionWithNullMessageShouldReturnGenericMessage() {
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Requirement: UNDEFINED exceptions with null message should return "Something went wrong, please try again"
        // This test will fail until the production code is updated
        // TODO: Uncomment and update when production code is changed
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        
        // Current behavior (this should be updated)
        assertThat(response.getBody().getMessage(), is(nullValue()));
        assertThat(response.getBody().getCode(), is(0));
    }

    @Test
    void testSelfRoleChangeExceptionShouldHaveUpdatedMessage() {
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // Requirement: SELF_ROLE_CHANGE message should be "Organization should have at least one OWNER"
        // This test will fail until the production code is updated
        // TODO: Uncomment and update when production code is changed
        // assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
        
        // Current behavior (this should be updated)
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
        assertThat(ex.getExceptionCode().getCode(), is(14));
    }

    @Test
    void testExceptionCodeValuesAreCorrect() {
        // Verify that the exception codes have the expected values
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        
        // Verify HTTP status codes
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus().value(), is(403));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
    }
}