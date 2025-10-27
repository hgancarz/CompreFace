package com.exadel.frs.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

class ExceptionCodeTest {

    @Test
    void testAccessDeniedCodeNameAndValues() {
        // Test that ACCESS_DENIED code name exists and has correct values
        // According to PR requirement, this should be changed to APP_ACCESS_DENIED
        assertThat(ExceptionCode.ACCESS_DENIED.name(), is(equalTo("ACCESS_DENIED")));
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(equalTo(1)));
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus(), is(equalTo(FORBIDDEN)));
    }

    @Test
    void testUndefinedCodeNameAndValues() {
        // Test that UNDEFINED code exists and has correct values
        assertThat(ExceptionCode.UNDEFINED.name(), is(equalTo("UNDEFINED")));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(equalTo(0)));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(equalTo(BAD_REQUEST)));
    }

    @Test
    void testSelfRoleChangeCodeNameAndValues() {
        // Test that SELF_ROLE_CHANGE code exists and has correct values
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.name(), is(equalTo("SELF_ROLE_CHANGE")));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(equalTo(14)));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus(), is(equalTo(BAD_REQUEST)));
    }
}