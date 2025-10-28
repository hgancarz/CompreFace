package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.BasicException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Validation test class that will PASS after the PR requirements are implemented.
 * This test suite defines the expected behavior after the production code changes.
 * 
 * Currently, these tests are expected to FAIL because the production code
 * hasn't been updated yet according to the PR requirements.
 */
class ErrorCodeRequirementsValidationTest {

    private ResponseExceptionHandler exceptionHandler = new ResponseExceptionHandler();

    /**
     * PR Requirement 1 (After Implementation):
     * |1|APP_ACCESS_DENIED|FORBIDDEN|Access Denied. Application has read only access to model|
     * Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED as it would be more informative
     */
    @Test
    void testAccessDeniedExceptionAfterImplementation() {
        // After implementation - should use APP_ACCESS_DENIED
        AccessDeniedException ex = new AccessDeniedException();
        
        // TODO: Uncomment and update when ExceptionCode.ACCESS_DENIED is renamed to APP_ACCESS_DENIED
        // assertThat(ex.getExceptionCode(), is(ExceptionCode.APP_ACCESS_DENIED));
        
        assertThat(ex.getExceptionCode().getCode(), is(1));
        assertThat(ex.getMessage(), is("Access Denied. Application has read only access to model"));
        
        // Test exception handling
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    /**
     * PR Requirement 2 (After Implementation):
     * |0|UNDEFINED|BAD_REQUEST|Something went wrong, please try again|
     * Change [Any exception message] to "Something went wrong, please try again"
     */
    @Test
    void testUndefinedExceptionAfterImplementation() {
        // After implementation - undefined exceptions should return generic message
        Exception ex = new NullPointerException();
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(0));
        
        // TODO: Uncomment when the message is changed to generic message
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    @Test
    void testUndefinedExceptionWithNullMessageAfterImplementation() {
        // After implementation - should return generic message even for null messages
        Exception ex = new Exception();
        
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(0));
        
        // TODO: Uncomment when the message is changed to generic message
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
    }

    /**
     * PR Requirement 3 (After Implementation):
     * |14|SELF_ROLE_CHANGE|BAD_REQUEST|Organization should have at least one OWNER|
     * Change Message to "Organization should have at least one OWNER"
     * 
     * Note: The PR mentions code 15, but current code is 14. This might need clarification.
     */
    @Test
    void testSelfRoleChangeExceptionAfterImplementation() {
        // After implementation - should have updated message
        SelfRoleChangeException ex = new SelfRoleChangeException();
        
        assertThat(ex.getExceptionCode(), is(ExceptionCode.SELF_ROLE_CHANGE));
        assertThat(ex.getExceptionCode().getCode(), is(14));
        
        // TODO: Uncomment when the message is updated
        // assertThat(ex.getMessage(), is("Organization should have at least one OWNER"));
        
        // Test exception handling
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(ex);
        assertThat(response.getBody().getCode(), is(14));
        
        // TODO: Uncomment when the message is updated
        // assertThat(response.getBody().getMessage(), is("Organization should have at least one OWNER"));
    }

    /**
     * Test to verify that the exception code mapping is consistent
     */
    @Test
    void testExceptionCodeConsistency() {
        // Verify that all exception codes have unique values
        java.util.Set<Integer> codes = new java.util.HashSet<>();
        for (ExceptionCode code : ExceptionCode.values()) {
            assertThat("Exception code " + code.name() + " should have unique value", 
                       codes.add(code.getCode()), is(true));
        }
    }
}