package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class ExceptionCodeTest {

    @Test
    void testAccessDeniedExceptionCodeAndMessage() {
        // Given
        AccessDeniedException exception = new AccessDeniedException();
        
        // Then
        assertThat(exception.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        assertThat(exception.getExceptionCode().getCode(), is(1));
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testSelfRoleChangeExceptionCodeAndMessage() {
        // Given
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        // Then
        assertThat(exception.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(exception.getExceptionCode().getCode(), is(14));
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testUndefinedExceptionCode() {
        // Given
        ExceptionCode undefinedCode = ExceptionCode.UNDEFINED;
        
        // Then
        assertThat(undefinedCode.getCode(), is(0));
        assertThat(undefinedCode.getHttpStatus().value(), is(400)); // BAD_REQUEST
    }

    @ParameterizedTest
    @MethodSource("exceptionCodeProvider")
    void testExceptionCodeValues(ExceptionCode exceptionCode, int expectedCode) {
        assertThat(exceptionCode.getCode(), is(expectedCode));
    }

    private static Stream<Arguments> exceptionCodeProvider() {
        return Stream.of(
                Arguments.of(ExceptionCode.ACCESS_DENIED, 1),
                Arguments.of(ExceptionCode.UNDEFINED, 0),
                Arguments.of(ExceptionCode.SELF_ROLE_CHANGE, 14)
        );
    }
}