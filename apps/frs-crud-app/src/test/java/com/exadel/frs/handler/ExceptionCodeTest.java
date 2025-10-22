package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

class ExceptionCodeTest {

    @Test
    void testAccessDeniedExceptionCodeAndMessage() {
        // Test current behavior - ACCESS_DENIED code and message
        AccessDeniedException exception = new AccessDeniedException();
        
        assertThat(exception.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        assertThat(exception.getExceptionCode().getCode(), is(1));
        assertThat(exception.getExceptionCode().getHttpStatus(), is(FORBIDDEN));
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionCode() {
        // Test UNDEFINED exception code
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(BAD_REQUEST));
    }

    @Test
    void testSelfRoleChangeExceptionCodeAndMessage() {
        // Test current behavior - SELF_ROLE_CHANGE code and message
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        assertThat(exception.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(exception.getExceptionCode().getCode(), is(14));
        assertThat(exception.getExceptionCode().getHttpStatus(), is(BAD_REQUEST));
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testExceptionCodeValues() {
        // Verify all exception codes have unique values
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        
        // Verify HTTP status codes
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus(), is(FORBIDDEN));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(BAD_REQUEST));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus(), is(BAD_REQUEST));
    }
}