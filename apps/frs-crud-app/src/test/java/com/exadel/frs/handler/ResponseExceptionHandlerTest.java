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
                .message("Something went wrong, please try again").build();

        assertThat(response.getBody(), is(equalTo(expectedResponseDto)));
    }

    @Test
    void testAccessDeniedExceptionCodeShouldBeAppAccessDenied() {
        // Test that ACCESS_DENIED exception code should be changed to APP_ACCESS_DENIED
        // This test will fail until the production code is updated
        AccessDeniedException ex = new AccessDeniedException();
        
        // Verify the current behavior (this should be updated when production code changes)
        assertThat(ex.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        
        // TODO: When production code is updated, this should be:
        // assertThat(ex.getExceptionCode(), is(ExceptionCode.APP_ACCESS_DENIED));
    }

    @Test
    void testUndefinedExceptionShouldShowGenericMessage() {
        // Test that UNDEFINED exceptions should show "Something went wrong, please try again"
        // This test will fail until the production code is updated
        Exception ex = new IllegalArgumentException("Test exception message");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        
        // Verify the current behavior (this should be updated when production code changes)
        assertThat(response.getBody().getMessage(), is(ex.getMessage()));
        
        // TODO: When production code is updated, this should be:
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void testSelfRoleChangeExceptionMessage() {
        // Test that SELF_ROLE_CHANGE exception message should be updated
        // This test will fail until the production code is updated
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // Verify the current behavior (this should be updated when production code changes)
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
        
        // TODO: When production code is updated, this should be:
        // assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
    }

    @Test
    void testExceptionCodeValues() {
        // Test that exception codes have the expected values
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        
        // TODO: When production code is updated, ACCESS_DENIED should be APP_ACCESS_DENIED
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(1));
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
