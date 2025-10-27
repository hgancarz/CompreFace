package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for exception handling behavior.
 * These tests verify that the ResponseExceptionHandler correctly processes different types of exceptions
 * and returns the appropriate HTTP status codes and response bodies.
 */
class ExceptionHandlingIntegrationTest {

    private final ResponseExceptionHandler exceptionHandler = new ResponseExceptionHandler();

    @Test
    void testAccessDeniedExceptionHandling() {
        // Given
        AccessDeniedException exception = new AccessDeniedException();

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode(), is(equalTo(ExceptionCode.ACCESS_DENIED.getHttpStatus())));
        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.ACCESS_DENIED.getCode())));
        assertThat(response.getBody().getMessage(), is(equalTo("Access Denied. Application has read only access to model")));
    }

    @Test
    void testSelfRoleChangeExceptionHandling() {
        // Given
        SelfRoleChangeException exception = new SelfRoleChangeException();

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode(), is(equalTo(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus())));
        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.SELF_ROLE_CHANGE.getCode())));
        assertThat(response.getBody().getMessage(), is(equalTo("Owner cannot change his own organization/application role")));
    }

    @Test
    void testUndefinedExceptionWithCustomMessage() {
        // Given
        String customMessage = "Custom error message";
        IllegalArgumentException exception = new IllegalArgumentException(customMessage);

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode(), is(equalTo(ExceptionCode.UNDEFINED.getHttpStatus())));
        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.UNDEFINED.getCode())));
        assertThat(response.getBody().getMessage(), is(equalTo(customMessage)));
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        // Given
        Exception exception = new Exception();

        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then
        assertThat(response.getStatusCode(), is(equalTo(ExceptionCode.UNDEFINED.getHttpStatus())));
        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.UNDEFINED.getCode())));
        // Current behavior - returns null message
        // After PR: should return "Something went wrong, please try again"
        assertThat(response.getBody().getMessage(), is(equalTo(null)));
    }

    @Test
    void testExceptionCodeConsistency() {
        // Verify that all defined exceptions have consistent HTTP status mappings
        // Note: code values don't necessarily match ordinal values, they are explicitly defined
        for (ExceptionCode code : ExceptionCode.values()) {
            assertThat(code.getHttpStatus().is4xxClientError(), is(true));
        }
    }
}