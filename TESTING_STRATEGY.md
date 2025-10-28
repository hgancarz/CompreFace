# Testing Strategy for PR Error Code Requirements

## Overview
This document outlines the testing strategy implemented to verify the PR requirements for error code and message changes.

## PR Requirements Summary

1. **ACCESS_DENIED → APP_ACCESS_DENIED**
   - Change code name from `ACCESS_DENIED` to `APP_ACCESS_DENIED`
   - Code: 1, HTTP Status: FORBIDDEN
   - Message remains: "Access Denied. Application has read only access to model"

2. **UNDEFINED Exception Message**
   - Change from "[Any exception message]" to "Something went wrong, please try again"
   - Code: 0, HTTP Status: BAD_REQUEST
   - This addresses the issue where null exception messages cause uninformative errors

3. **SELF_ROLE_CHANGE Message**
   - Change from "Owner cannot change his own organization" to "Organization should have at least one OWNER"
   - Code: 15 (Note: Current code is 14 - needs clarification)
   - HTTP Status: BAD_REQUEST

## Test Files Created

### 1. Enhanced ResponseExceptionHandlerTest.java
- **Location**: `/repo/apps/frs-crud-app/src/test/java/com/exadel/frs/handler/ResponseExceptionHandlerTest.java`
- **Purpose**: Enhanced existing test with specific tests for error codes and messages
- **Tests Added**:
  - `testAccessDeniedExceptionCodeAndMessage()`: Verifies ACCESS_DENIED code (1) and message
  - `testSelfRoleChangeExceptionCodeAndMessage()`: Verifies SELF_ROLE_CHANGE code (14) and message
  - `testUndefinedExceptionCode()`: Verifies UNDEFINED code (0)
  - Extended parameterized tests to include more exception types

### 2. ErrorCodeRequirementsTest.java
- **Location**: `/repo/apps/frs-crud-app/src/test/java/com/exadel/frs/handler/ErrorCodeRequirementsTest.java`
- **Purpose**: Documents current behavior and will fail when production code changes are made
- **Tests**:
  - Tests current behavior for all three PR requirements
  - Serves as baseline validation
  - Will need updates after production code changes

### 3. ErrorCodeRequirementsValidationTest.java
- **Location**: `/repo/apps/frs-crud-app/src/test/java/com/exadel/frs/handler/ErrorCodeRequirementsValidationTest.java`
- **Purpose**: Defines expected behavior after PR implementation
- **Tests**:
  - Contains TODO comments for changes needed
  - Will pass after production code is updated
  - Serves as acceptance criteria

## Test Execution Results

### Current Status (Before Production Changes)
- ✅ All tests pass with current implementation
- ✅ Test coverage includes all three PR requirements
- ✅ Tests verify both exception codes and messages

### After Production Changes
When the production code is updated according to the PR requirements:

1. **ErrorCodeRequirementsTest.java** will FAIL
   - This is expected as it tests current behavior
   - Should be updated or removed after changes

2. **ErrorCodeRequirementsValidationTest.java** will PASS
   - Remove TODO comments as changes are implemented
   - This becomes the main validation test

3. **ResponseExceptionHandlerTest.java** may need updates
   - Update test names and assertions for renamed constants
   - Verify new message formats

## Production Code Changes Required

Based on the PR requirements, the following production code changes are needed:

### 1. ExceptionCode.java
- Rename `ACCESS_DENIED` to `APP_ACCESS_DENIED`
- Verify SELF_ROLE_CHANGE code (14 vs 15 mentioned in PR)

### 2. ResponseExceptionHandler.java
- Update `handleUndefinedExceptions` to return generic message:
  ```java
  // Current: message(ex.getMessage())
  // Should be: message("Something went wrong, please try again")
  ```

### 3. SelfRoleChangeException.java
- Update message from:
  ```java
  // Current: "Owner cannot change his own organization/application role"
  // Should be: "Organization should have at least one OWNER"
  ```

## Notes and Clarifications Needed

1. **Code Discrepancy**: PR mentions SELF_ROLE_CHANGE code 15, but current code is 14
2. **Message Specificity**: The PR suggests changing SELF_ROLE_CHANGE message, but the current message includes "/application role" which might be intentional
3. **Generic Message**: The suggested "Something went wrong, please try again" for UNDEFINED exceptions should be confirmed with BAs

## Next Steps

1. Implement production code changes according to PR requirements
2. Update test files accordingly
3. Run full test suite to ensure all tests pass
4. Update confluence documentation as mentioned in PR