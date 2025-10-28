package com.exadel.frs.handler;

import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;

/**
 * Utility class to verify exception messages and codes according to PR requirements.
 * This can be used in various test classes to ensure consistency.
 */
public class ExceptionMessageVerifier {

    /**
     * Verifies the current state of AccessDeniedException.
     * After PR requirement 1 is implemented, this should verify:
     * - Exception code: APP_ACCESS_DENIED (currently ACCESS_DENIED)
     * - Code value: 1
     * - Message: "Access Denied. Application has read only access to model"
     */
    public static void verifyAccessDeniedException(AccessDeniedException exception) {
        // Current state verification
        assert ExceptionCode.ACCESS_DENIED.equals(exception.getExceptionCode()) : 
            "Expected ACCESS_DENIED exception code, but got: " + exception.getExceptionCode();
        assert 1 == exception.getExceptionCode().getCode() : 
            "Expected code 1, but got: " + exception.getExceptionCode().getCode();
        assert "Access Denied. Application has read only access to model".equals(exception.getMessage()) : 
            "Expected message 'Access Denied. Application has read only access to model', but got: " + exception.getMessage();
        
        // After requirement 1 is implemented, the above assertions should be updated to:
        // assert ExceptionCode.APP_ACCESS_DENIED.equals(exception.getExceptionCode());
        // assert 1 == exception.getExceptionCode().getCode();
        // assert "Access Denied. Application has read only access to model".equals(exception.getMessage());
    }

    /**
     * Verifies the current state of SelfRoleChangeException.
     * After PR requirement 3 is implemented, this should verify:
     * - Exception code: SELF_ROLE_CHANGE
     * - Code value: 14
     * - Message: "Organization should have at least one OWNER" (currently "Owner cannot change his own organization/application role")
     */
    public static void verifySelfRoleChangeException(SelfRoleChangeException exception) {
        // Current state verification
        assert ExceptionCode.SELF_ROLE_CHANGE.equals(exception.getExceptionCode()) : 
            "Expected SELF_ROLE_CHANGE exception code, but got: " + exception.getExceptionCode();
        assert 14 == exception.getExceptionCode().getCode() : 
            "Expected code 14, but got: " + exception.getExceptionCode().getCode();
        assert "Owner cannot change his own organization/application role".equals(exception.getMessage()) : 
            "Expected message 'Owner cannot change his own organization/application role', but got: " + exception.getMessage();
        
        // After requirement 3 is implemented, the message assertion should be updated to:
        // assert "Organization should have at least one OWNER".equals(exception.getMessage());
    }

    /**
     * Returns the expected generic message for undefined exceptions.
     * After PR requirement 2 is implemented, this should return:
     * "Something went wrong, please try again"
     */
    public static String getExpectedUndefinedExceptionMessage() {
        // Currently undefined exceptions return their original message
        // After requirement 2 is implemented, this should return:
        // return "Something went wrong, please try again";
        
        // For now, return null to indicate that the original message should be preserved
        return null;
    }
}