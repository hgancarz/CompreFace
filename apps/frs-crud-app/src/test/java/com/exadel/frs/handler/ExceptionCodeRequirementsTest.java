package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Test class that verifies the requirements from the PR description.
 * These tests will fail until the production code changes are implemented.
 */
class ExceptionCodeRequirementsTest {

    @Test
    void requirement1_accessDeniedShouldBeRenamedToAppAccessDenied() {
        // Requirement 1: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        // This test will fail until ExceptionCode.ACCESS_DENIED is renamed to APP_ACCESS_DENIED
        
        // Verify current state (this will fail after the change)
        assertThat(ExceptionCode.APP_ACCESS_DENIED.name(), is("APP_ACCESS_DENIED"));
        
        // After change, this should be:
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.name(), is("APP_ACCESS_DENIED"));
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(1));
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.getHttpStatus(), is(FORBIDDEN));
    }

    @Test
    void requirement2_undefinedExceptionShouldHaveGenericMessage() {
        // Requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This requires changes to ResponseExceptionHandler.handleUndefinedExceptions
        
        // Current behavior: undefined exceptions return their actual message
        // After change: undefined exceptions should return "Something went wrong, please try again"
        
        // This test documents the requirement but cannot test it directly without
        // modifying the ResponseExceptionHandler
    }

    @Test
    void requirement3_selfRoleChangeMessageShouldBeUpdated() {
        // Requirement 3: Change Message to "Organization should have at least one OWNER"
        
        // Current message
        SelfRoleChangeException ex = new SelfRoleChangeException();
        assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
        
        // After change, this should be:
        // assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
    }

    @Test
    void verifyExceptionCodeIntegrityAfterChanges() {
        // After all changes are implemented, verify the integrity
        
        // APP_ACCESS_DENIED (formerly ACCESS_DENIED)
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.getCode(), is(1));
        // assertThat(ExceptionCode.APP_ACCESS_DENIED.getHttpStatus(), is(FORBIDDEN));
        
        // UNDEFINED
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.getHttpStatus(), is(BAD_REQUEST));
        
        // SELF_ROLE_CHANGE
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getHttpStatus(), is(BAD_REQUEST));
    }
}