package com.exadel.frs.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.FORBIDDEN;

class AccessDeniedExceptionTest {

    @Test
    void fieldsShouldBeConsistent() {
        AccessDeniedException ex = new AccessDeniedException();
        assertEquals("Access Denied. Application has read only access to model", ex.getMessage());
        assertEquals(1, ex.getExceptionCode().getCode().intValue());
        assertEquals(FORBIDDEN, ex.getExceptionCode().getHttpStatus());
    }
}
