package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExceptionCodeTest {

    @Test
    void shouldHaveAccessDeniedCodeWithCorrectValues() {
        // Test current ACCESS_DENIED code values
        ExceptionCode accessDenied = ExceptionCode.ACCESS_DENIED;
        assertThat(accessDenied.getCode(), is(1));
        assertThat(accessDenied.getHttpStatus(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    void shouldHaveCorrectAccessDeniedExceptionMessage() {
        // Test that AccessDeniedException has the correct message
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getMessage(), 
            is("Access Denied. Application has read only access to model"));
    }

    @Test
    void shouldHaveCurrentSelfRoleChangeExceptionMessage() {
        // Test current SelfRoleChangeException message
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getMessage(), 
            is("Owner cannot change his own organization/application role"));
    }

    @Test
    void shouldHaveUndefinedExceptionCode() {
        // Test that UNDEFINED exception code exists with correct values
        ExceptionCode undefined = ExceptionCode.UNDEFINED;
        assertThat(undefined.getCode(), is(0));
        assertThat(undefined.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
    }
}