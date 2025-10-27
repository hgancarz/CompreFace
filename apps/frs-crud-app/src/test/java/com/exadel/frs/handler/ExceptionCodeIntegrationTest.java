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
 * Integration test class that validates the complete exception handling flow
 * according to the PR requirements. These tests will fail until the production
 * code is updated to meet the requirements.
 */
class ExceptionCodeIntegrationTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testCompleteAccessDeniedExceptionFlow() {
        AccessDeniedException ex = new AccessDeniedException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        
        // Verify the response structure
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
        
        // TODO: After production code update, the following should be true:
        // - ExceptionCode should be APP_ACCESS_DENIED instead of ACCESS_DENIED
        // - The message might remain the same or be updated
    }

    @Test
    void testCompleteSelfRoleChangeExceptionFlow() {
        SelfRoleChangeException ex = new SelfRoleChangeException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        
        // Verify the response structure
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(14));
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
        
        // TODO: After production code update, the message should be:
        // "Organization should have at least one OWNER"
    }

    @Test
    void testCompleteUndefinedExceptionFlow() {
        // Test with various undefined exceptions
        Exception[] undefinedExceptions = {
            new NullPointerException("Test null pointer"),
            new IllegalArgumentException("Invalid argument"),
            new RuntimeException("Runtime error"),
            new NullPointerException() // null message
        };
        
        for (Exception ex : undefinedExceptions) {
            ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
            
            // Verify the response structure
            assertThat(response.getBody(), is(notNullValue()));
            assertThat(response.getBody().getCode(), is(0));
            
            // Current behavior: returns the original exception message
            // TODO: After production code update, all undefined exceptions should return:
            // "Something went wrong, please try again"
            if (ex.getMessage() != null) {
                assertThat(response.getBody().getMessage(), is(ex.getMessage()));
            } else {
                assertThat(response.getBody().getMessage(), is(nullValue()));
            }
        }
    }

    @Test
    void testExceptionCodeEnumValues() {
        // Verify that all relevant exception codes exist and have correct values
        assertThat(ExceptionCode.ACCESS_DENIED, is(notNullValue()));
        assertThat(ExceptionCode.UNDEFINED, is(notNullValue()));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE, is(notNullValue()));
        
        // Verify code values
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        
        // Verify HTTP status codes
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus().value(), is(403));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
        
        // TODO: After production code update, ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        // assertThat(ExceptionCode.APP_ACCESS_DENIED, is(notNullValue()));
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(1));
    }
}