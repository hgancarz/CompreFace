package com.exadel.frs.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExceptionMessageTest {

    @Test
    void testAccessDeniedExceptionMessage() {
        // Test that AccessDeniedException has the correct message
        AccessDeniedException exception = new AccessDeniedException();
        
        assertThat(exception.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testSelfRoleChangeExceptionMessage() {
        // Test that SelfRoleChangeException has the correct message
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testBasicExceptionConstruction() {
        // Test that BasicException properly stores exception code and message
        AccessDeniedException accessDeniedException = new AccessDeniedException();
        
        assertThat(accessDeniedException.getExceptionCode().getCode(), is(1));
        assertThat(accessDeniedException.getMessage(), is("Access Denied. Application has read only access to model"));
        
        SelfRoleChangeException selfRoleChangeException = new SelfRoleChangeException();
        
        assertThat(selfRoleChangeException.getExceptionCode().getCode(), is(14));
        assertThat(selfRoleChangeException.getMessage(), is("Owner cannot change his own organization/application role"));
    }
}