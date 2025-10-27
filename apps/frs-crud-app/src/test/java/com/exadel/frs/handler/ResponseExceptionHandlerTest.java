package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.BasicException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class ResponseExceptionHandlerTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @ParameterizedTest
    @MethodSource("definedExceptions")
    void handleDefinedExceptions(final BasicException ex) {
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);

        ExceptionResponseDto expectedResponseDto = ExceptionResponseDto.builder()
                .code(ex.getExceptionCode().getCode())
                .message(ex.getMessage()).build();

        assertThat(response.getBody(), is(equalTo(expectedResponseDto)));
    }

    @ParameterizedTest
    @MethodSource("undefinedExceptions")
    void handleUndefinedExceptions(final Exception ex) {
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        ExceptionResponseDto expectedResponseDto = ExceptionResponseDto.builder()
                .code(ExceptionCode.UNDEFINED.getCode())
                .message(ex.getMessage()).build();

        assertThat(response.getBody(), is(equalTo(expectedResponseDto)));
    }

    @Test
    void testAccessDeniedExceptionCodeAndMessage() {
        AccessDeniedException ex = new AccessDeniedException();
        
        // Test that ACCESS_DENIED code is 1 (this will need to be updated to APP_ACCESS_DENIED)
        assertThat(ex.getExceptionCode().getCode(), is(1));
        assertThat(ex.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        
        // Test the current message
        assertThat(ex.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testSelfRoleChangeExceptionCodeAndMessage() {
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // Test that SELF_ROLE_CHANGE code is 14
        assertThat(ex.getExceptionCode().getCode(), is(14));
        assertThat(ex.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        
        // Test the current message (this will need to be updated)
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testUndefinedExceptionCodeAndMessage() {
        Exception ex = new NullPointerException("Test null pointer");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Test that UNDEFINED code is 0
        assertThat(response.getBody().getCode(), is(0));
        
        // Test the current behavior (this will need to be updated to return a generic message)
        assertThat(response.getBody().getMessage(), is("Test null pointer"));
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Test that UNDEFINED code is 0
        assertThat(response.getBody().getCode(), is(0));
        
        // Test the current behavior with null message (this will need to be updated)
        // Currently returns null, but should return a generic message
        assertThat(response.getBody().getMessage(), is((String) null));
    }

    private static Stream<Arguments> definedExceptions() {
        return Stream.of(
                Arguments.of(new AccessDeniedException()),
                Arguments.of(new SelfRoleChangeException())
        );
    }

    private static Stream<Arguments> undefinedExceptions() {
        return Stream.of(
                Arguments.of(new NullPointerException("Test exception")),
                Arguments.of(new IllegalArgumentException("Invalid argument")),
                Arguments.of(new RuntimeException("Runtime error"))
        );
    }
}
