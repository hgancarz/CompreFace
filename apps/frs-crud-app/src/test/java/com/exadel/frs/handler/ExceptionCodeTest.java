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
    void testAccessDeniedCode() {
        // Test current behavior - should be updated when code name changes to APP_ACCESS_DENIED
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(equalTo(1)));
        assertThat(ExceptionCode.ACCESS_DENIED.getHttpStatus(), is(equalTo(FORBIDDEN)));
    }

    @Test
    void testUndefinedCode() {
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(equalTo(0)));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(equalTo(BAD_REQUEST)));
    }

    @Test
    void testSelfRoleChangeCode() {
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(equalTo(14)));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus(), is(equalTo(BAD_REQUEST)));
    }

    @Test
    void testAllExceptionCodesHaveUniqueCodes() {
        // Ensure all exception codes have unique numeric codes
        long uniqueCodeCount = java.util.Arrays.stream(ExceptionCode.values())
                .map(ExceptionCode::getCode)
                .distinct()
                .count();
        
        assertThat(uniqueCodeCount, is(equalTo((long) ExceptionCode.values().length)));
    }

    @Test
    void testAllExceptionCodesHaveValidHttpStatus() {
        // Ensure all exception codes have valid HTTP status codes
        for (ExceptionCode code : ExceptionCode.values()) {
            assertThat(code.getHttpStatus().is4xxClientError(), is(true));
        }
    }
}