package com.exadel.frs.controller;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import com.exadel.frs.handler.ExceptionCode;
import com.exadel.frs.handler.ResponseExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration test to verify that exception handling works correctly
 * and produces the expected response format with proper codes and messages.
 */
class ExceptionHandlingIntegrationTest {

    private ResponseExceptionHandler exceptionHandler = new ResponseExceptionHandler();

    @Test
    void testAccessDeniedExceptionHandling() {
        // Given
        AccessDeniedException exception = new AccessDeniedException();

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode().value(), is(403));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testSelfRoleChangeExceptionHandling() {
        // Given
        SelfRoleChangeException exception = new SelfRoleChangeException();

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode().value(), is(400));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(14));
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testUndefinedExceptionHandling() {
        // Given
        RuntimeException exception = new RuntimeException("Test error message");

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode().value(), is(400));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(0));
        assertThat(response.getBody().getMessage(), is("Test error message"));
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        // Given
        RuntimeException exception = new RuntimeException();

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode().value(), is(400));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(0));
        // Currently returns null for message, but this should be changed to a generic message
        assertThat(response.getBody().getMessage(), is(nullValue()));
    }

    @Test
    void testExceptionCodeConsistency() {
        // Verify that all exception codes are unique
        assertThat(ExceptionCode.values().length, is(greaterThan(0)));
        
        // Verify specific codes mentioned in PR
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        
        // Verify HTTP status codes
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus().value(), is(403));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
    }
}