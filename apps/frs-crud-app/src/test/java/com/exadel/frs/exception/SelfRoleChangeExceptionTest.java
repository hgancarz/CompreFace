package com.exadel.frs.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SelfRoleChangeExceptionTest {

    @Test
    void messageShouldBeInformativeAndBackwardCompatible() {
        SelfRoleChangeException ex = new SelfRoleChangeException();
        String msg = ex.getMessage();
        assertTrue(
                "Owner cannot change his own organization".equals(msg)
                        || "Owner cannot change his own organization/application role".equals(msg)
                        || "Organization should have at least one OWNER".equals(msg),
                "Unexpected SELF_ROLE_CHANGE message: " + msg
        );
    }
}
