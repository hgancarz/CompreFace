package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExceptionCodeTest {

    @Test
    void testAccessDeniedExceptionCodeAndMessage() {
        // Test that ACCESS_DENIED code is 1 and has FORBIDDEN status
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus(), is(HttpStatus.FORBIDDEN));
        
        // Test that AccessDeniedException uses ACCESS_DENIED code
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionCodeAndMessage() {
        // Test that UNDEFINED code is 0 and has BAD_REQUEST status
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testSelfRoleChangeExceptionCodeAndMessage() {
        // Test that SELF_ROLE_CHANGE code is 14 and has BAD_REQUEST status
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        
        // Test that SelfRoleChangeException uses SELF_ROLE_CHANGE code
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
    }
}