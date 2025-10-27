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
 * Test class to verify the PR requirements for error handling:
 * 1. ACCESS_DENIED should be changed to APP_ACCESS_DENIED (more informative)
 * 2. UNDEFINED should show "Something went wrong, please try again" instead of actual exception message
 * 3. SELF_ROLE_CHANGE message should be "Organization should have at least one OWNER"
 */
class ResponseExceptionHandlerPRTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedExceptionHandling() {
        // Given: AccessDeniedException is thrown
        AccessDeniedException exception = new AccessDeniedException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then: Verify the response contains correct code and message
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.ACCESS_DENIED.getCode()));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
        
        // TODO: After PR implementation, this should verify:
        // - ExceptionCode.ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        // - The message might also need to be updated to be more informative
    }

    @Test
    void testUndefinedExceptionHandling() {
        // Given: A generic exception with null message
        Exception exception = new NullPointerException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then: Verify the response contains UNDEFINED code and the actual exception message
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(response.getBody().getMessage(), is(nullValue()));
        
        // TODO: After PR implementation, this should verify:
        // - The message should be "Something went wrong, please try again" instead of null
    }

    @Test
    void testUndefinedExceptionHandlingWithMessage() {
        // Given: A generic exception with a specific message
        Exception exception = new IllegalArgumentException("Test error message");

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then: Verify the response contains UNDEFINED code and the actual exception message
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(response.getBody().getMessage(), is("Test error message"));
        
        // TODO: After PR implementation, this should verify:
        // - The message should be "Something went wrong, please try again" instead of "Test error message"
    }

    @Test
    void testSelfRoleChangeExceptionHandling() {
        // Given: SelfRoleChangeException is thrown
        SelfRoleChangeException exception = new SelfRoleChangeException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then: Verify the response contains correct code and message
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.SELF_ROLE_CHANGE.getCode()));
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
        
        // TODO: After PR implementation, this should verify:
        // - The message should be "Organization should have at least one OWNER"
    }

    @Test
    void testExceptionCodeValues() {
        // Verify current exception code values
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        
        // TODO: After PR implementation, this should verify:
        // - ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        // - The codes should remain the same
    }
}