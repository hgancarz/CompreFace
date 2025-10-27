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
    void testUndefinedExceptionWithNullMessage() {
        // Test that undefined exceptions with null messages don't cause issues
        Exception ex = new Exception();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.UNDEFINED.getCode())));
        // Current behavior - should be updated when message changes to "Something went wrong, please try again"
        assertThat(response.getBody().getMessage(), is(equalTo(null)));
    }

    @Test
    void testAccessDeniedExceptionResponse() {
        // Test specific ACCESS_DENIED exception response
        AccessDeniedException ex = new AccessDeniedException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);

        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.ACCESS_DENIED.getCode())));
        assertThat(response.getBody().getMessage(), is(equalTo("Access Denied. Application has read only access to model")));
        assertThat(response.getStatusCode(), is(equalTo(ExceptionCode.ACCESS_DENIED.getHttpStatus())));
    }

    @Test
    void testSelfRoleChangeExceptionResponse() {
        // Test specific SELF_ROLE_CHANGE exception response
        SelfRoleChangeException ex = new SelfRoleChangeException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);

        assertThat(response.getBody().getCode(), is(equalTo(ExceptionCode.SELF_ROLE_CHANGE.getCode())));
        assertThat(response.getBody().getMessage(), is(equalTo("Owner cannot change his own organization/application role")));
        assertThat(response.getStatusCode(), is(equalTo(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus())));
    }

    private static Stream<Arguments> definedExceptions() {
        return Stream.of(
                Arguments.of(new AccessDeniedException()),
                Arguments.of(new SelfRoleChangeException())
        );
    }

    private static Stream<Arguments> undefinedExceptions() {
        return Stream.of(
                Arguments.of(new NullPointerException()),
                Arguments.of(new IllegalArgumentException("Test exception")),
                Arguments.of(new RuntimeException())
        );
    }
}
