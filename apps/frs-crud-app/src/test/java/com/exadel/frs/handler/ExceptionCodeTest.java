package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExceptionCodeTest {

    @Test
    void testAccessDeniedExceptionCode() {
        // Test that ACCESS_DENIED exception code exists and has correct values
        ExceptionCode accessDenied = ExceptionCode.ACCESS_DENIED;
        
        assertThat(accessDenied.getCode(), is(1));
        assertThat(accessDenied.getHttpStatus(), is(HttpStatus.FORBIDDEN));
        
        // Test AccessDeniedException message
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
        assertThat(exception.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
    }

    @Test
    void testUndefinedExceptionCode() {
        // Test that UNDEFINED exception code exists and has correct values
        ExceptionCode undefined = ExceptionCode.UNDEFINED;
        
        assertThat(undefined.getCode(), is(0));
        assertThat(undefined.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testSelfRoleChangeExceptionCode() {
        // Test that SELF_ROLE_CHANGE exception code exists and has correct values
        ExceptionCode selfRoleChange = ExceptionCode.SELF_ROLE_CHANGE;
        
        assertThat(selfRoleChange.getCode(), is(14));
        assertThat(selfRoleChange.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        
        // Test SelfRoleChangeException message
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
        assertThat(exception.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
    }

    @Test
    void testExceptionCodeUniqueness() {
        // Test that all exception codes have unique numeric values
        ExceptionCode[] codes = ExceptionCode.values();
        
        for (int i = 0; i < codes.length; i++) {
            for (int j = i + 1; j < codes.length; j++) {
                assertThat("Exception codes should be unique", 
                    codes[i].getCode(), 
                    not(equalTo(codes[j].getCode())));
            }
        }
    }
}