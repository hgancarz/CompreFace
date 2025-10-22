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
    void handleUndefinedExceptionsWithNullMessage() {
        // Test that undefined exceptions with null message return a default message
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        ExceptionResponseDto expectedResponseDto = ExceptionResponseDto.builder()
                .code(ExceptionCode.UNDEFINED.getCode())
                .message(ex.getMessage()).build();

        assertThat(response.getBody(), is(equalTo(expectedResponseDto)));
    }

    @Test
    void verifyAccessDeniedExceptionCodeAndMessage() {
        // Test that ACCESS_DENIED exception code is 1 and message is correct
        AccessDeniedException ex = new AccessDeniedException();
        
        assertThat(ex.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        assertThat(ex.getExceptionCode().getCode(), is(1));
        assertThat(ex.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void verifySelfRoleChangeExceptionCodeAndMessage() {
        // Test that SELF_ROLE_CHANGE exception code is 14 and message is correct
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        assertThat(ex.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(ex.getExceptionCode().getCode(), is(14));
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void verifyUndefinedExceptionCode() {
        // Test that UNDEFINED exception code is 0
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
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
                Arguments.of(new IllegalArgumentException("Test exception"))
        );
    }
}
