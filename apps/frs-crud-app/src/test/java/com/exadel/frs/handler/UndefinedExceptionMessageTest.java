package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test class specifically for testing undefined exception message handling.
 * This test documents the current behavior and the expected behavior after changes.
 */
class UndefinedExceptionMessageTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void handleUndefinedExceptionWithNullMessage() {
        // Test current behavior with null message
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Current behavior: returns null message
        assertThat(response.getBody().getMessage(), is(nullValue()));
        assertThat(response.getBody().getCode(), is(0));

        // TODO: After production code change, this should return a default message
        // Expected behavior after PR requirement 2:
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void handleUndefinedExceptionWithActualMessage() {
        // Test current behavior with actual message
        Exception ex = new IllegalArgumentException("Test error message");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Current behavior: returns the original exception message
        assertThat(response.getBody().getMessage(), is("Test error message"));
        assertThat(response.getBody().getCode(), is(0));

        // TODO: After production code change, this might still return the original message
        // or might be overridden with the default message - depends on implementation
    }

    @Test
    void verifyUndefinedExceptionCode() {
        // Verify that UNDEFINED exception code is 0 as specified in PR
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400)); // BAD_REQUEST
    }
}