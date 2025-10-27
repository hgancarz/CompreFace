package com.exadel.frs.exception;

import com.exadel.frs.handler.ExceptionCode;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class SelfRoleChangeExceptionTest {

    @Test
    void testSelfRoleChangeException() {
        // Test current behavior - should be updated when message changes
        SelfRoleChangeException exception = new SelfRoleChangeException();
        
        assertThat(exception.getExceptionCode(), is(equalTo(ExceptionCode.SELF_ROLE_CHANGE)));
        assertThat(exception.getMessage(), is(equalTo("Owner cannot change his own organization/application role")));
    }
}