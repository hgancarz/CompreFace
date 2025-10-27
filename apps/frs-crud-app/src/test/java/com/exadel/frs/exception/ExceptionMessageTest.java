package com.exadel.frs.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class ExceptionMessageTest {

    @Test
    void testAccessDeniedExceptionMessage() {
        // Test that AccessDeniedException has the correct message
        // According to PR requirement, this message should remain the same
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getMessage(), is(equalTo("Access Denied. Application has read only access to model")));
    }

    @Test
    void testSelfRoleChangeExceptionMessage() {
        // Test that SelfRoleChangeException has the correct message
        // According to PR requirement, this should be changed to "Organization should have at least one OWNER"
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getMessage(), is(equalTo("Owner cannot change his own organization/application role")));
    }
}