package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ResponseExceptionHandlerMessageTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void shouldReturnDefaultMessageForUndefinedExceptionsWithNullMessage() {
        // Test requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This test will fail until the production code is updated
        Exception ex = new NullPointerException();
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(response.getBody().getMessage(), 
            is("Something went wrong, please try again"));
    }

    @Test
    void shouldReturnDefaultMessageForUndefinedExceptionsWithEmptyMessage() {
        // Test requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This test will fail until the production code is updated
        Exception ex = new Exception("");
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(response.getBody().getMessage(), 
            is("Something went wrong, please try again"));
    }

    @Test
    void shouldReturnDefaultMessageForUndefinedExceptionsWithNullPointerException() {
        // Test requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This test will fail until the production code is updated
        Exception ex = new NullPointerException("null");
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(response.getBody().getMessage(), 
            is("Something went wrong, please try again"));
    }
}