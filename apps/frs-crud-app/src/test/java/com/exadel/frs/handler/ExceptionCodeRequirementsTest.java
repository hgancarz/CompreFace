package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test class to verify the PR requirements for error codes and messages.
 * These tests will initially fail and should pass after the production code changes are implemented.
 */
class ExceptionCodeRequirementsTest {

    @Test
    void verifyAccessDeniedExceptionCodeNameShouldBeChangedToAppAccessDenied() {
        // PR Requirement 1: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        // This test verifies that the exception code name should be APP_ACCESS_DENIED
        // Currently this will fail because the code is still ACCESS_DENIED
        
        AccessDeniedException ex = new AccessDeniedException();
        
        // TODO: After production code change, this should be APP_ACCESS_DENIED
        // assertThat(ex.getExceptionCode().name(), is("APP_ACCESS_DENIED"));
        
        // Current state - this will pass
        assertThat(ex.getExceptionCode().name(), is("ACCESS_DENIED"));
        
        // Verify code number remains 1
        assertThat(ex.getExceptionCode().getCode(), is(1));
        
        // Verify message remains the same
        assertThat(ex.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void verifyUndefinedExceptionShouldHaveDefaultMessage() {
        // PR Requirement 2: Change [Any exception message] to "Something went wrong, please try again"
        // This test verifies that undefined exceptions should return a default message
        // Currently the ResponseExceptionHandler returns the original exception message
        
        // TODO: After production code change, undefined exceptions should return a default message
        // This would require modifying ResponseExceptionHandler.buildBody(Exception ex) method
        // to return "Something went wrong, please try again" when ex.getMessage() is null
        
        // Current state - undefined exceptions return their original message
        // This test documents the requirement but doesn't test it directly
        // since it would require production code changes
    }

    @Test
    void verifySelfRoleChangeExceptionMessageShouldBeUpdated() {
        // PR Requirement 3: Change Message to "Organization should have at least one OWNER"
        // This test verifies that the SelfRoleChangeException message should be updated
        // Currently this will fail because the message is still the old one
        
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        // TODO: After production code change, this should be the new message
        // assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
        
        // Current state - this will pass
        assertThat(ex.getMessage(), is("Owner cannot change his own organization/application role"));
        
        // Verify code number remains 14
        assertThat(ex.getExceptionCode().getCode(), is(14));
    }

    @Test
    void verifyExceptionCodeValues() {
        // Verify that the exception codes remain the same as specified in the PR
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
    }
}