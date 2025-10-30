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
        // Test that ACCESS_DENIED exception code exists with correct values
        ExceptionCode accessDeniedCode = ExceptionCode.ACCESS_DENIED;
        
        assertThat(accessDeniedCode.getCode(), is(1));
        assertThat(accessDeniedCode.getHttpStatus(), is(HttpStatus.FORBIDDEN));
        
        // Test that AccessDeniedException uses the correct code and message
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionCode() {
        // Test that UNDEFINED exception code exists with correct values
        ExceptionCode undefinedCode = ExceptionCode.UNDEFINED;
        
        assertThat(undefinedCode.getCode(), is(0));
        assertThat(undefinedCode.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testSelfRoleChangeExceptionCodeAndMessage() {
        // Test that SELF_ROLE_CHANGE exception code exists with correct values
        ExceptionCode selfRoleChangeCode = ExceptionCode.SELF_ROLE_CHANGE;
        
        assertThat(selfRoleChangeCode.getCode(), is(14));
        assertThat(selfRoleChangeCode.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        
        // Test that SelfRoleChangeException uses the correct code and message
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
    }
}