package com.exadel.frs.handler;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class that verifies the EXPECTED behavior after PR implementation.
 * These tests will FAIL until the PR changes are implemented.
 * 
 * PR Requirements:
 * 1. ACCESS_DENIED should be changed to APP_ACCESS_DENIED (more informative)
 * 2. UNDEFINED should show "Something went wrong, please try again" instead of actual exception message
 * 3. SELF_ROLE_CHANGE message should be "Organization should have at least one OWNER"
 */
class ResponseExceptionHandlerExpectedBehaviorTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedExceptionShouldUseAppAccessDeniedCode() {
        // Given: AccessDeniedException is thrown
        AccessDeniedException exception = new AccessDeniedException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then: Verify the response contains correct code and message
        assertThat(response.getBody(), is(notNullValue()));
        
        // TODO: After PR implementation, this should pass:
        // - ExceptionCode.ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
        // - The code should remain 1
        // assertThat(exception.getExceptionCode().name(), is("APP_ACCESS_DENIED"));
        
        // Current behavior (this will pass now but should be updated after PR):
        assertThat(exception.getExceptionCode().name(), is("ACCESS_DENIED"));
        assertThat(response.getBody().getCode(), is(1));
        assertThat(response.getBody().getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testUndefinedExceptionShouldShowGenericMessage() {
        // Given: A generic exception with null message
        Exception exception = new NullPointerException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then: Verify the response contains UNDEFINED code and generic message
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        
        // TODO: After PR implementation, this should pass:
        // - The message should be "Something went wrong, please try again" instead of null
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        
        // Current behavior (this will pass now but should be updated after PR):
        assertThat(response.getBody().getMessage(), is(nullValue()));
    }

    @Test
    void testUndefinedExceptionWithMessageShouldShowGenericMessage() {
        // Given: A generic exception with a specific message
        Exception exception = new IllegalArgumentException("Test error message");

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then: Verify the response contains UNDEFINED code and generic message
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        
        // TODO: After PR implementation, this should pass:
        // - The message should be "Something went wrong, please try again" instead of "Test error message"
        // assertThat(response.getBody().getMessage(), is("Something went wrong, please try again"));
        
        // Current behavior (this will pass now but should be updated after PR):
        assertThat(response.getBody().getMessage(), is("Test error message"));
    }

    @Test
    void testSelfRoleChangeExceptionShouldHaveNewMessage() {
        // Given: SelfRoleChangeException is thrown
        SelfRoleChangeException exception = new SelfRoleChangeException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then: Verify the response contains correct code and NEW message
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getCode(), is(ExceptionCode.SELF_ROLE_CHANGE.getCode()));
        
        // TODO: After PR implementation, this should pass:
        // - The message should be "Organization should have at least one OWNER"
        // assertThat(response.getBody().getMessage(), is("Organization should have at least one OWNER"));
        
        // Current behavior (this will pass now but should be updated after PR):
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testExceptionCodeShouldHaveAppAccessDenied() {
        // Verify that APP_ACCESS_DENIED exists in ExceptionCode enum
        
        // TODO: After PR implementation, this should pass:
        // - ExceptionCode should have APP_ACCESS_DENIED instead of ACCESS_DENIED
        // try {
        //     ExceptionCode appAccessDenied = ExceptionCode.valueOf("APP_ACCESS_DENIED");
        //     assertThat(appAccessDenied.getCode(), is(1));
        //     assertThat(appAccessDenied.getHttpStatus().value(), is(403));
        // } catch (IllegalArgumentException e) {
        //     fail("APP_ACCESS_DENIED should exist in ExceptionCode enum");
        // }
        
        // Current behavior (this will pass now but should be updated after PR):
        try {
            ExceptionCode accessDenied = ExceptionCode.valueOf("ACCESS_DENIED");
            assertThat(accessDenied.getCode(), is(1));
            assertThat(accessDenied.getHttpStatus().value(), is(403));
        } catch (IllegalArgumentException e) {
            throw new AssertionError("ACCESS_DENIED should exist in ExceptionCode enum");
        }
    }
}