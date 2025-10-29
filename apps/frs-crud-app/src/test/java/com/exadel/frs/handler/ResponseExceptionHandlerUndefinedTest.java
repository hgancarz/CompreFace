package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test class specifically for testing undefined exception handling behavior.
 * This tests requirement #2 from the PR description.
 */
class ResponseExceptionHandlerUndefinedTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void handleUndefinedExceptionsWithNullMessage() {
        // Test current behavior with null message
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Current behavior: returns the actual exception message (which is null)
        assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        
        // After requirement #2 is implemented, this should be:
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void handleUndefinedExceptionsWithActualMessage() {
        // Test current behavior with actual message
        String exceptionMessage = "Test exception message";
        Exception ex = new IllegalArgumentException(exceptionMessage);
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Current behavior: returns the actual exception message
        assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        
        // After requirement #2 is implemented, this should be:
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void requirement2_undefinedExceptionsShouldReturnGenericMessage() {
        // This test documents the requirement for undefined exceptions
        // Currently, undefined exceptions return their actual message
        // After requirement #2 is implemented, ALL undefined exceptions should return:
        // "Something went wrong, please try again"
        
        // Test cases that should all return the same generic message:
        Exception[] testExceptions = {
            new NullPointerException(),
            new IllegalArgumentException("Test"),
            new RuntimeException("Another test"),
            new Exception("Generic exception")
        };

        for (Exception ex : testExceptions) {
            ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
            
            // Current behavior - returns actual message
            assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
            
            // After requirement #2 - should all return the same generic message
            // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        }
    }
}