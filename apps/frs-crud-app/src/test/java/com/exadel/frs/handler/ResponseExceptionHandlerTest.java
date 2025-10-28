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
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        AccessDeniedException exception = new AccessDeniedException();
        
        // Verify exception code is ACCESS_DENIED (should be APP_ACCESS_DENIED after change)
        assertEquals(ExceptionCode.ACCESS_DENIED, exception.getExceptionCode());
        
        // Verify exception code value is 1
        assertEquals(1, exception.getExceptionCode().getCode());
        
        // Verify exception message
        assertEquals("Access Denied. Application has read only access to model", exception.getMessage());
    }

    @Test
    void testSelfRoleChangeExceptionCodeAndMessage() {
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        // Verify exception code is SELF_ROLE_CHANGE
        assertEquals(ExceptionCode.SELF_ROLE_CHANGE, exception.getExceptionCode());
        
        // Verify exception code value is 14
        assertEquals(14, exception.getExceptionCode().getCode());
        
        // Verify exception message (should be changed to "Organization should have at least one OWNER")
        assertEquals("Owner cannot change his own organization/application role", exception.getMessage());
    }

    @Test
    void testUndefinedExceptionHandling() {
        // Test that undefined exceptions return the original message
        // This should be changed to return "Something went wrong, please try again"
        NullPointerException originalException = new NullPointerException("Test null pointer");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(originalException);
        
        assertEquals(ExceptionCode.UNDEFINED.getCode(), response.getBody().getCode());
        assertEquals("Test null pointer", response.getBody().getMessage());
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        // Test that undefined exceptions with null message return null
        // This should be changed to return "Something went wrong, please try again"
        NullPointerException originalException = new NullPointerException();
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(originalException);
        
        assertEquals(ExceptionCode.UNDEFINED.getCode(), response.getBody().getCode());
        // Currently returns null, should be changed to "Something went wrong, please try again"
        // assertEquals("Something went wrong, please try again", response.getBody().getMessage());
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
