package com.exadel.frs.handler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * This test class documents the expected future state after the PR changes are implemented.
 * These tests are currently disabled and should be enabled once the production code changes are made.
 */
@Disabled("Enable these tests after implementing PR changes")
class ExceptionCodeFutureStateTest {

    @Test
    void testAppAccessDeniedCodeName() {
        // After PR: ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        // This test will fail until the enum constant name is changed
        // Uncomment and update when the change is made:
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(equalTo(1)));
    }

    @Test
    void testUndefinedExceptionMessage() {
        // After PR: UNDEFINED exceptions should return a fixed message instead of the actual exception message
        // This test documents the expected behavior but cannot be implemented without production code changes
        // The ResponseExceptionHandler.buildBody(Exception) method should be updated to return:
        // "Something went wrong, please try again" instead of ex.getMessage()
    }

    @Test
    void testSelfRoleChangeMessage() {
        // After PR: SELF_ROLE_CHANGE message should be updated
        // This test documents the expected behavior but cannot be implemented without production code changes
        // The SelfRoleChangeException.MESSAGE should be changed to:
        // "Organization should have at least one OWNER"
    }
}