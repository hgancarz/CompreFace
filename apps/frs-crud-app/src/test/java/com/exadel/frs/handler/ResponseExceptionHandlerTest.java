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
    void handleUndefinedExceptionWithNullMessage() {
        // Given
        Exception ex = new NullPointerException();
        
        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Then
        ExceptionResponseDto expectedResponseDto = ExceptionResponseDto.builder()
                .code(ExceptionCode.UNDEFINED.getCode())
                .message(null).build();

        assertThat(response.getBody(), is(equalTo(expectedResponseDto)));
    }

    @Test
    void testAccessDeniedExceptionResponse() {
        // Given
        AccessDeniedException ex = new AccessDeniedException();
        
        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        
        // Then
        assertThat(response.getStatusCode().value(), is(403)); // FORBIDDEN
        assertThat(response.getBody().getCode(), is(1)); // ACCESS_DENIED code
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testSelfRoleChangeExceptionResponse() {
        // Given
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // When
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        
        // Then
        assertThat(response.getStatusCode().value(), is(400)); // BAD_REQUEST
        assertThat(response.getBody().getCode(), is(14)); // SELF_ROLE_CHANGE code
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
    }

    private static Stream<Arguments> definedExceptions() {
        return Stream.of(
                Arguments.of(new AccessDeniedException()),
                Arguments.of(new SelfRoleChangeException())
        );
    }

    private static Stream<Arguments> undefinedExceptions() {
        return Stream.of(
                Arguments.of(new NullPointerException("test message")),
                Arguments.of(new IllegalArgumentException("invalid argument"))
        );
    }
}
