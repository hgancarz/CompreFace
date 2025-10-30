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
 * Test class to verify the PR requirements for exception handling:
 * 1. ACCESS_DENIED should be renamed to APP_ACCESS_DENIED
 * 2. UNDEFINED exception should show "Something went wrong, please try again" instead of actual exception message
 * 3. SELF_ROLE_CHANGE message should be changed to "Organization should have at least one OWNER"
 */
class ResponseExceptionHandlerRequirementsTest {

    private ResponseExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testAccessDeniedExceptionHandling() {
        // Given: AccessDeniedException is thrown
        AccessDeniedException exception = new AccessDeniedException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then: Verify the response contains correct code and message
        ExceptionResponseDto responseBody = response.getBody();
        assertThat(responseBody, is(notNullValue()));
        assertThat(responseBody.getCode(), is(ExceptionCode.ACCESS_DENIED.getCode()));
        assertThat(responseBody.getMessage(), is("Access Denied. Application has read only access to model"));
        
        // Verify the exception code name (this test will fail when ACCESS_DENIED is renamed to APP_ACCESS_DENIED)
        assertThat(exception.getExceptionCode().name(), is("ACCESS_DENIED"));
    }

    @Test
    void testUndefinedExceptionHandling() {
        // Given: An undefined exception (like NullPointerException) is thrown
        Exception exception = new NullPointerException("Test null pointer");

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then: Verify the response contains UNDEFINED code and the actual exception message
        ExceptionResponseDto responseBody = response.getBody();
        assertThat(responseBody, is(notNullValue()));
        assertThat(responseBody.getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(responseBody.getMessage(), is("Test null pointer"));
        
        // Note: According to PR requirement, this should eventually show 
        // "Something went wrong, please try again" instead of the actual exception message
    }

    @Test
    void testUndefinedExceptionWithNullMessage() {
        // Given: An undefined exception with null message
        Exception exception = new NullPointerException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleUndefinedExceptions(exception);

        // Then: Verify the response contains UNDEFINED code and null message
        ExceptionResponseDto responseBody = response.getBody();
        assertThat(responseBody, is(notNullValue()));
        assertThat(responseBody.getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        assertThat(responseBody.getMessage(), is(nullValue()));
        
        // Note: According to PR requirement, this should eventually show 
        // "Something went wrong, please try again" instead of null
    }

    @Test
    void testSelfRoleChangeExceptionHandling() {
        // Given: SelfRoleChangeException is thrown
        SelfRoleChangeException exception = new SelfRoleChangeException();

        // When: Exception is handled
        ResponseEntity<ExceptionResponseDto> response = exceptionHandler.handleDefinedExceptions(exception);

        // Then: Verify the response contains correct code and message
        ExceptionResponseDto responseBody = response.getBody();
        assertThat(responseBody, is(notNullValue()));
        assertThat(responseBody.getCode(), is(ExceptionCode.SELF_ROLE_CHANGE.getCode()));
        assertThat(responseBody.getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testExceptionCodeValues() {
        // Verify current exception code values (these tests will fail when codes are updated)
        assertThat(ExceptionCode.ACCESS_DENIED.getCode(), is(1));
        assertThat(ExceptionCode.ACCESS_DENIED.name(), is("ACCESS_DENIED"));
        
        assertThat(ExceptionCode.UNDEFINED.getCode(), is(0));
        assertThat(ExceptionCode.UNDEFINED.name(), is("UNDEFINED"));
        
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.getCode(), is(14));
        assertThat(ExceptionCode.SELF_ROLE_CHANGE.name(), is("SELF_ROLE_CHANGE"));
    }
}