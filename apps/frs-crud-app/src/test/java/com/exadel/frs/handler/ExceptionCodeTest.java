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
    void verifyExceptionCodeValues() {
        // Test current values - these should be updated according to PR requirements
        
        // ACCESS_DENIED should be changed to APP_ACCESS_DENIED
        assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.APP_ACCESS_DENIED.getHttpStatus(), is(FORBIDDEN));
        
        // UNDEFINED should have code 0 and BAD_REQUEST status
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(BAD_REQUEST));
        
        // SELF_ROLE_CHANGE should have code 14 and BAD_REQUEST status
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus(), is(BAD_REQUEST));
    }
    
    @Test
    void verifyExceptionCodeNames() {
        // Test that the enum names are as expected
        // This test will need to be updated when ACCESS_DENIED is renamed to APP_ACCESS_DENIED
        assertThat(ExceptionCode.APP_ACCESS_DENIED.name(), is("APP_ACCESS_DENIED"));
        assertThat(ExceptionCode.UNDEFINED.name(), is("UNDEFINED"));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.name(), is("SELF_ROLE_CHANGE"));
    }
}