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
    void testAccessDeniedExceptionCodeIsAppAccessDenied() {
        // Test that ACCESS_DENIED code name should be APP_ACCESS_DENIED
        // This test will fail until the production code is updated
        AccessDeniedException exception = new AccessDeniedException();
        
        // Verify the exception code is APP_ACCESS_DENIED (after change)
        assertThat(exception.getExceptionCode(), is(ExceptionCode.APP_ACCESS_DENIED));
        
        // Verify the message is correct
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionHasGenericMessage() {
        // Test that UNDEFINED exceptions return a generic message instead of the original exception message
        // This test will fail until the production code is updated
        Exception originalException = new NullPointerException("null");
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(originalException);
        
        // After change, this should return the generic message
        assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void testSelfRoleChangeExceptionHasUpdatedMessage() {
        // Test that SELF_ROLE_CHANGE exception has the updated message
        // This test will fail until the production code is updated
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        // After change, this should return the new message
        assertThat(exception.getMessage(), is("Organization should have at least one OWNER"));
    }

    @Test
    void testExceptionCodeValues() {
        // Test that all exception codes have the expected values
        assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.APP_ACCESS_DENIED.getHttpStatus().value(), is(403));
        
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus().value(), is(400));
        
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus().value(), is(400));
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
