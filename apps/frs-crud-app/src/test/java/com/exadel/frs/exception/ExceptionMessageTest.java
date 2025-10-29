package com.exadel.frs.exception;

import com.exadel.frs.handler.ExceptionCode;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class ExceptionMessageTest {

    @Test
    void verifyAccessDeniedExceptionMessage() {
        // Current message - should remain the same according to PR
        AccessDeniedException ex = new AccessDeniedException();
        assertThat(ex.getMessage(), is("Access Denied. Application has read only access to model"));
        assertThat(ex.getExceptionCode(), is(ExceptionCode.APP_ACCESS_DENIED));
    }

    @Test
    void verifySelfRoleChangeExceptionMessage() {
        // Current message - should be changed to "Organization should have at least one OWNER"
        SelfRoleChangeException ex = new SelfRoleChangeException();
        assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
        assertThat(ex.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
    }
    
    @Test
    void verifyBasicExceptionStructure() {
        // Test that BasicException properly stores code and message
        String testMessage = "Test message";
        BasicException ex = new BasicException(ExceptionCode.UNDEFINED, testMessage) {};
        
        assertThat(ex.getMessage(), is(testMessage));
        assertThat(ex.getExceptionCode(), is(ExceptionCode.UNDEFINED));
    }
}