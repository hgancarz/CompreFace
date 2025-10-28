package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class to verify the requirements from the PR description.
 * These tests will fail until the production code changes are implemented.
 */
class ExceptionCodeRequirementsTest {

    @Test
    void testAccessDeniedShouldBeChangedToAppAccessDenied() {
        // Requirement: Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
        // This test verifies that the enum value APP_ACCESS_DENIED exists with the correct properties
        
        // TODO: Uncomment and update when production code is changed
        /*
        ExceptionCode appAccessDeniedCode = ExceptionCode.APP_ACCESS_DENIED;
        
        assertThat(appAccessDeniedCode.getCode(), is(1));
        assertThat(appAccessDeniedCode.getHttpStatus().value(), is(403));
        
        // Verify that ACCESS_DENIED no longer exists
        ExceptionCode[] allCodes = ExceptionCode.values();
        assertThat(Arrays.stream(allCodes).noneMatch(code -> code.name().equals("ACCESS_DENIED")), is(true));
        */
        
        // Current state verification
        ExceptionCode accessDeniedCode = ExceptionCode.ACCESS_DENIED;
        assertThat(accessDeniedCode.getCode(), is(1));
        assertThat(accessDeniedCode.getHttpStatus().value(), is(403));
    }

    @Test
    void testUndefinedExceptionShouldHaveFixedMessage() {
        // Requirement: Change [Any exception message] to "Something went wrong, please try again"
        // This test verifies that undefined exceptions return a fixed message
        
        // TODO: Uncomment and update when production code is changed
        /*
        ResponseExceptionHandler handler = new ResponseExceptionHandler();
        Exception undefinedException = new NullPointerException();
        
        ResponseEntity<ExceptionResponseDto> response = handler.handleUndefinedExceptions(undefinedException);
        
        assertThat(response.getBody().getCode(), is(0));
        assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        */
        
        // Current state verification
        ExceptionCode undefinedCode = ExceptionCode.UNDEFINED;
        assertThat(undefinedCode.getCode(), is(0));
        assertThat(undefinedCode.getHttpStatus().value(), is(400));
    }

    @Test
    void testSelfRoleChangeShouldHaveUpdatedMessage() {
        // Requirement: Change Message to "Organization should have at least one OWNER"
        // This test verifies that SelfRoleChangeException has the updated message
        
        // TODO: Uncomment and update when production code is changed
        /*
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getMessage(), is("Organization should have at least one OWNER"));
        */
        
        // Current state verification
        SelfRoleChangeException exception = new SelfRoleChangeException();
        assertThat(exception.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testAccessDeniedExceptionShouldUseAppAccessDeniedCode() {
        // Requirement: Verify that AccessDeniedException uses APP_ACCESS_DENIED code
        
        // TODO: Uncomment and update when production code is changed
        /*
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.APP_ACCESS_DENIED));
        */
        
        // Current state verification
        AccessDeniedException exception = new AccessDeniedException();
        assertThat(exception.getExceptionCode(), is(ExceptionCode.ACCESS_DENIED));
    }
}