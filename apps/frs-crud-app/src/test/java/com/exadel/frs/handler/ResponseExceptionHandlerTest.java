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
        
        // Verify the exception code is ACCESS_DENIED (should be changed to APP_ACCESS_DENIED)
        assertThat(ex.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        
        // Verify the current message (should be changed to more informative message)
        assertThat(ex.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testSelfRoleChangeExceptionMessage() {
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // Verify the exception code
        assertThat(ex.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        
        // Verify the current message (should be changed to "Organization should have at least one OWNER")
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testUndefinedExceptionHandling() {
        Exception ex = new NullPointerException("Test null pointer");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Currently returns the actual exception message
        // Should be changed to return "Something went wrong, please try again"
        assertThat(response.getBody().getMessage(), is("Test null pointer"));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        Exception ex = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);

        // Currently returns null message
        // Should be changed to return "Something went wrong, please try again"
        assertThat(response.getBody().getMessage(), is((String) null));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
    }

    private static Stream<Arguments> definedExceptions() {
        return Stream.of(
                Arguments.of(new AccessDeniedException()),
                Arguments.of(new SelfRoleChangeException())
        );
    }

    private static Stream<Arguments> undefinedExceptions() {
        return Stream.of(
                Arguments.of(new NullPointerException("Test message")),
                Arguments.of(new IllegalArgumentException("Invalid argument")),
                Arguments.of(new RuntimeException("Runtime error"))
        );
    }
}
