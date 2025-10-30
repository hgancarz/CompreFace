package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Documentation test that clearly shows the current state vs expected state
 * for the PR requirements. This test documents what needs to be changed.
 */
class ExceptionCodeDocumentationTest {

    @Test
    void documentCurrentVsExpectedBehavior() {
        // Requirement 1: ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        System.out.println("=== REQUIREMENT 1: ACCESS_DENIED Renaming ===");
        System.out.println("Current: ExceptionCode.ACCESS_DENIED.name() = " + ExceptionCode.ACCESS_DENIED.name());
        System.out.println("Expected: ExceptionCode.APP_ACCESS_DENIED should exist");
        System.out.println("Current code: " + ExceptionCode.ACCESS_DENIED.getCode());
        System.out.println("Expected code: 1 (should remain the same)");
        
        // Requirement 2: UNDEFINED exception should show generic message
        System.out.println("\n=== REQUIREMENT 2: UNDEFINED Exception Message ===");
        System.out.println("Current: Shows actual exception message (or null)");
        System.out.println("Expected: Should show 'Something went wrong, please try again'");
        System.out.println("Current code: " + ExceptionCode.UNDEFINED.getCode());
        System.out.println("Expected code: 0 (should remain the same)");
        
        // Requirement 3: SELF_ROLE_CHANGE message update
        System.out.println("\n=== REQUIREMENT 3: SELF_ROLE_CHANGE Message Update ===");
        SelfRoleChangeException currentException = new SelfRoleChangeException();
        System.out.println("Current message: " + currentException.getMessage());
        System.out.println("Expected message: 'Organization should have at least one OWNER'");
        System.out.println("Current code: " + ExceptionCode.SELF_ROLE_CHANGE.getCode());
        System.out.println("Expected code: 14 (should remain the same)");
        
        // Verify current state (these assertions should remain true until changes are made)
        assertThat(ExceptionCode.ACCESS_DENIED.name(), is("ACCESS_DENIED"));
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        
        assertThat(ExceptionCode.UNDEFINED.name(), is("UNDEFINED"));
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.name(), is("SELF_ROLE_CHANGE"));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(currentException.getMessage(), is("Owner cannot change his own organization/application role"));
    }
    
    @Test
    void documentFilesThatNeedToBeUpdated() {
        System.out.println("\n=== FILES THAT NEED TO BE UPDATED ===");
        System.out.println("1. ExceptionCode.java - Rename ACCESS_DENIED to APP_ACCESS_DENIED");
        System.out.println("2. AccessDeniedException.java - Update import to use APP_ACCESS_DENIED");
        System.out.println("3. SelfRoleChangeException.java - Update message to 'Organization should have at least one OWNER'");
        System.out.println("4. ResponseExceptionHandler.java - Update undefined exception handling to show generic message");
        System.out.println("5. Any other files that reference ACCESS_DENIED constant");
    }
}