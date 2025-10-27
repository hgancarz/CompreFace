# Testing Strategy for PR Requirements

## Overview
This document outlines the testing strategy implemented to verify the PR requirements for error code and message changes.

## PR Requirements
1. **ACCESS_DENIED → APP_ACCESS_DENIED**: Change code name from `ACCESS_DENIED` to `APP_ACCESS_DENIED`
2. **UNDEFINED error message**: Change from showing actual exception message to "Something went wrong, please try again"
3. **SELF_ROLE_CHANGE message**: Change from "Owner cannot change his own organization/application role" to "Organization should have at least one OWNER"

## Test Files Created

### 1. ExceptionCodeTest.java
- Tests the current values of error codes
- Verifies that `ACCESS_DENIED`, `UNDEFINED`, and `SELF_ROLE_CHANGE` codes exist with correct values

### 2. ExceptionMessageTest.java  
- Tests the current exception messages
- Verifies that `AccessDeniedException` and `SelfRoleChangeException` have the expected messages

### 3. ResponseExceptionHandlerTest.java (Enhanced)
- Enhanced existing test with additional test cases
- Tests specific handling of `AccessDeniedException` and `SelfRoleChangeException`
- Tests undefined exception handling with null and actual messages

### 4. ExceptionCodeRequirementsTest.java (Key Test File)
- **This is the main test file that will fail until production changes are made**
- Contains 4 test methods that verify the PR requirements:
  - `testAccessDeniedCodeNameShouldBeAppAccessDenied()` - Fails until `ACCESS_DENIED` is renamed to `APP_ACCESS_DENIED`
  - `testUndefinedExceptionShouldShowGenericMessage()` - Fails until undefined exceptions show generic message
  - `testSelfRoleChangeExceptionShouldHaveNewMessage()` - Fails until SelfRoleChangeException message is updated
  - `testUndefinedExceptionWithActualMessageShouldShowGenericMessage()` - Fails until all undefined exceptions show generic message

## Production Code Changes Required

### 1. ExceptionCode.java
```java
// Change from:
ACCESS_DENIED(1, FORBIDDEN)
// To:
APP_ACCESS_DENIED(1, FORBIDDEN)
```

### 2. ResponseExceptionHandler.java
```java
// In buildBody(final Exception ex) method:
// Change from:
.message(ex.getMessage())
// To:
.message("Something went wrong, please try again")
```

### 3. SelfRoleChangeException.java
```java
// Change from:
public static final String MESSAGE = "Owner cannot change his own organization/application role";
// To:
public static final String MESSAGE = "Organization should have at least one OWNER";
```

## Test Execution Results

### Current State (Before Production Changes)
- **ExceptionCodeRequirementsTest**: 4 tests FAILING (as expected)
- All other tests: PASSING

### Expected State (After Production Changes)  
- **All tests**: PASSING

## Verification Strategy

1. **Before changes**: Run `ExceptionCodeRequirementsTest` - should fail with 4 failures
2. **After changes**: Run `ExceptionCodeRequirementsTest` - should pass all 4 tests
3. **Regression testing**: Run all existing tests to ensure no regressions

## Files to Update in Documentation
After production changes are implemented, update the Confluence page:
- https://confluence.exadel.com/display/EFRS/FRS+REST+Errors

Update the following error codes and messages:
- `ACCESS_DENIED` → `APP_ACCESS_DENIED`
- `UNDEFINED` error message → "Something went wrong, please try again"
- `SELF_ROLE_CHANGE` message → "Organization should have at least one OWNER"