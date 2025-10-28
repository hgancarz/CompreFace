package com.exadel.frs.core.trainservice.util;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Utility class for validating status endpoint responses according to PR requirements.
 */
public class StatusResponseValidator {

    /**
     * Returns a set of ResultMatchers that validate the status response meets all PR requirements.
     * 
     * @return array of ResultMatchers for validating the status response
     */
    public static ResultMatcher[] validateStatusResponse() {
        return new ResultMatcher[]{
                jsonPath("$.status", is("OK")),
                jsonPath("$.build_version", not(emptyOrNullString())),
                jsonPath("$.build_version", not(emptyString())),
                jsonPath("$.calculator_version", not(emptyOrNullString())),
                jsonPath("$.calculator_version", not(emptyString())),
                jsonPath("$.similarity_coefficients", notNullValue()),
                jsonPath("$.available_plugins", notNullValue())
        };
    }

    /**
     * Validates that all required fields are present in the status response.
     * 
     * @return array of ResultMatchers for validating field presence
     */
    public static ResultMatcher[] validateRequiredFieldsPresent() {
        return new ResultMatcher[]{
                jsonPath("$.status").exists(),
                jsonPath("$.build_version").exists(),
                jsonPath("$.calculator_version").exists(),
                jsonPath("$.similarity_coefficients").exists(),
                jsonPath("$.available_plugins").exists()
        };
    }

    /**
     * Validates that version fields are non-empty strings.
     * 
     * @return array of ResultMatchers for validating version fields
     */
    public static ResultMatcher[] validateVersionFields() {
        return new ResultMatcher[]{
                jsonPath("$.build_version", not(emptyOrNullString())),
                jsonPath("$.build_version", not(emptyString())),
                jsonPath("$.calculator_version", not(emptyOrNullString())),
                jsonPath("$.calculator_version", not(emptyString()))
        };
    }

    /**
     * Validates that similarity_coefficients is present as an array.
     * 
     * @param expectedSize expected size of the similarity coefficients array
     * @return array of ResultMatchers for validating similarity coefficients
     */
    public static ResultMatcher[] validateSimilarityCoefficients(int expectedSize) {
        return new ResultMatcher[]{
                jsonPath("$.similarity_coefficients", notNullValue()),
                jsonPath("$.similarity_coefficients", hasSize(expectedSize))
        };
    }

    /**
     * Validates that available_plugins is present as an object.
     * 
     * @param expectedSize expected size of the available plugins map
     * @return array of ResultMatchers for validating available plugins
     */
    public static ResultMatcher[] validateAvailablePlugins(int expectedSize) {
        return new ResultMatcher[]{
                jsonPath("$.available_plugins", notNullValue()),
                jsonPath("$.available_plugins", aMapWithSize(expectedSize))
        };
    }
}