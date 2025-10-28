package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExceptionCodeTest {

    @Test
    void testAccessDeniedExceptionCodeAndMessage() {
        // Test that ACCESS_DENIED exception code exists and has correct values
        ExceptionCode accessDeniedCode = ExceptionCode.ACCESS_DENIED;
        
        assertThat(accessDeniedCode.getCode(), is(1));
        assertThat(accessDeniedCode.getHttpStatus().value(), is(403));
        
        // Test that AccessDeniedException uses the correct code and message
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionCode() {
        // Test that UNDEFINED exception code exists and has correct values
        ExceptionCode undefinedCode = ExceptionCode.UNDEFINED;
        
        assertThat(undefinedCode.getCode(), is(0));
        assertThat(undefinedCode.getHttpStatus().value(), is(400));
    }

    @Test
    void testSelfRoleChangeExceptionCodeAndMessage() {
        // Test that SELF_ROLE_CHANGE exception code exists and has correct values
        ExceptionCode selfRoleChangeCode = ExceptionCode.SELF_ROLE_CHANGE;
        
        assertThat(selfRoleChangeCode.getCode(), is(14));
        assertThat(selfRoleChangeCode.getHttpStatus().value(), is(400));
        
        // Test that SelfRoleChangeException uses the correct code and message
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testExceptionCodeUniqueness() {
        // Test that all exception codes are unique
        ExceptionCode[] codes = ExceptionCode.values();
        
        for (int i = 0; i < codes.length; i++) {
            for (int j = i + 1; j < codes.length; j++) {
                assertThat("Exception codes should be unique", 
                    codes[i].getCode(), not(equalTo(codes[j].getCode())));
            }
        }
    }
}