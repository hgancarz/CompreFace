package com.exadel.frs.exception;

import com.exadel.frs.handler.ExceptionCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionCodeTest {

    @Test
    void testAccessDeniedExceptionCode() {
        // Test that ACCESS_DENIED has the correct code and HTTP status
        ExceptionCode accessDenied = ExceptionCode.ACCESS_DENIED;
        
        assertThat(accessDenied.getCode(), is(1));
        assertThat(accessDenied.getHttpStatus(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    void testUndefinedExceptionCode() {
        // Test that UNDEFINED has the correct code and HTTP status
        ExceptionCode undefined = ExceptionCode.UNDEFINED;
        
        assertThat(undefined.getCode(), is(0));
        assertThat(undefined.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testSelfRoleChangeExceptionCode() {
        // Test that SELF_ROLE_CHANGE has the correct code and HTTP status
        ExceptionCode selfRoleChange = ExceptionCode.SELF_ROLE_CHANGE;
        
        assertThat(selfRoleChange.getCode(), is(14));
        assertThat(selfRoleChange.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
    }
}