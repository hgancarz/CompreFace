package com.exadel.frs.exception;

import com.exadel.frs.handler.ExceptionCode;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class AccessDeniedExceptionTest {

    @Test
    void testAccessDeniedException() {
        // Test current behavior - should be updated when message changes
        AccessDeniedException exception = new AccessDeniedException();
        
        assertThat(exception.getExceptionCode(), is(equalTo(ExceptionCode.ACCESS_DENIED)));
        assertThat(exception.getMessage(), is(equalTo("Access Denied. Application has read only access to model")));
    }
}