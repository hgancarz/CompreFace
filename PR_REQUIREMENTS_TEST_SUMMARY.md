# PR Requirements Test Summary

## Overview
This document summarizes the test coverage created to verify the PR requirements. The tests currently fail because the production code needs to be updated according to the PR description.

## PR Requirements

### 1. Change Code Name ACCESS_DENIED to APP_ACCESS_DENIED
**Current State:** `ExceptionCode.ACCESS_DENIED` exists with code 1 and FORBIDDEN status
**Expected State:** `ExceptionCode.APP_ACCESS_DENIED` should exist with code 1 and FORBIDDEN status

**Test Coverage:**
- `PRRequirementsTest.requirement1_shouldHaveAppAccessDeniedCodeInsteadOfAccessDenied()`
- `ExceptionCodeTest.shouldHaveAccessDeniedCodeWithCorrectValues()`

**Production Code Changes Needed:**
- Update `ExceptionCode.java` to rename `ACCESS_DENIED` to `APP_ACCESS_DENIED`
- Update all references to `ACCESS_DENIED` in the codebase

### 2. Change [Any exception message] to "Something went wrong, please try again"
**Current State:** Undefined exceptions return the actual exception message (which can be null or uninformative)
**Expected State:** All undefined exceptions should return "Something went wrong, please try again"

**Test Coverage:**
- `PRRequirementsTest.requirement2_shouldReturnDefaultMessageForUndefinedExceptions()`
- `PRRequirementsTest.requirement2_edgeCase_shouldHandleNullPointerExceptionWithNullMessage()`
- `PRRequirementsTest.requirement2_edgeCase_shouldHandleExceptionWithEmptyMessage()`
- `ResponseExceptionHandlerMessageTest` (all tests)
- `ErrorResponseIntegrationTest.shouldReturnDefaultMessageForUndefinedExceptions()`

**Production Code Changes Needed:**
- Update `ResponseExceptionHandler.java` to use a default message in `buildBody(Exception ex)` method
- Replace `ex.getMessage()` with "Something went wrong, please try again"

### 3. Change Message to "Organization should have at least one OWNER"
**Current State:** `SelfRoleChangeException.MESSAGE = "Owner cannot change his own organization/application role"`
**Expected State:** `SelfRoleChangeException.MESSAGE = "Organization should have at least one OWNER"`

**Test Coverage:**
- `PRRequirementsTest.requirement3_shouldHaveUpdatedSelfRoleChangeMessage()`
- `ExceptionCodeTest.shouldHaveCurrentSelfRoleChangeExceptionMessage()`
- `ErrorResponseIntegrationTest.shouldReturnSelfRoleChangeErrorWithUpdatedMessage()`

**Production Code Changes Needed:**
- Update `SelfRoleChangeException.java` to change the MESSAGE constant

## Test Files Created

1. **`ExceptionCodeTest.java`** - Tests current exception code values and messages
2. **`ResponseExceptionHandlerMessageTest.java`** - Tests undefined exception message handling
3. **`PRRequirementsTest.java`** - Comprehensive tests for all PR requirements
4. **`ErrorResponseIntegrationTest.java`** - Integration tests for error responses

## Current Test Results
- **Total Tests:** 130
- **Failures:** 9 (all related to PR requirements)
- **Errors:** 0
- **Skipped:** 0

## Next Steps
1. Update production code according to the changes outlined above
2. Run tests to verify all requirements are met
3. Update the Confluence documentation as mentioned in the PR description

## Notes
- The tests are designed to fail with the current implementation
- Once production code changes are made, all tests should pass
- The test failures provide clear documentation of what behavior is expected