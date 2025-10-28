package com.exadel.frs.core.trainservice.controller;

import com.exadel.frs.commonservice.sdk.faces.FacesApiClient;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.FacesStatusResponse;
import com.exadel.frs.core.trainservice.EmbeddedPostgreSQLTest;
import com.exadel.frs.core.trainservice.config.IntegrationTest;
import com.exadel.frs.core.trainservice.util.StatusResponseValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.exadel.frs.core.trainservice.system.global.Constants.API_V1;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for GET /status endpoint as described in the PR requirements.
 * 
 * Acceptance criteria:
 * - GET /status returns HTTP 200
 * - Response JSON contains all required keys: "status", "build_version", "calculator_version", "similarity_coefficients", "available_plugins"
 * - "status" equals "OK"
 * - "build_version" and "calculator_version" are non-empty strings
 * - "similarity_coefficients" key is present (array or object)
 * - Automated test validates presence and non-empty values
 */
@IntegrationTest
@AutoConfigureMockMvc
@DisplayName("GET /status endpoint integration test")
class StatusEndpointIntegrationTest extends EmbeddedPostgreSQLTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FacesApiClient facesApiClient;

    @Test
    @DisplayName("Should return HTTP 200 status code")
    void shouldReturnHttp200() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should contain all required JSON keys")
    void shouldContainAllRequiredJsonKeys() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.build_version").exists())
                .andExpect(jsonPath("$.calculator_version").exists())
                .andExpect(jsonPath("$.similarity_coefficients").exists())
                .andExpect(jsonPath("$.available_plugins").exists());
    }

    @Test
    @DisplayName("Should have status field equal to 'OK'")
    void shouldHaveStatusEqualToOk() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    @DisplayName("Should have non-empty build_version and calculator_version fields")
    void shouldHaveNonEmptyVersionFields() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(jsonPath("$.build_version", not(emptyOrNullString())))
                .andExpect(jsonPath("$.build_version", not(emptyString())))
                .andExpect(jsonPath("$.calculator_version", not(emptyOrNullString())))
                .andExpect(jsonPath("$.calculator_version", not(emptyString())));
    }

    @Test
    @DisplayName("Should have similarity_coefficients field present as array")
    void shouldHaveSimilarityCoefficientsAsArray() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(jsonPath("$.similarity_coefficients", notNullValue()))
                .andExpect(jsonPath("$.similarity_coefficients", hasSize(3)))
                .andExpect(jsonPath("$.similarity_coefficients[0]", is(0.6)))
                .andExpect(jsonPath("$.similarity_coefficients[1]", is(0.7)))
                .andExpect(jsonPath("$.similarity_coefficients[2]", is(0.8)));
    }

    @Test
    @DisplayName("Should have available_plugins field present as object")
    void shouldHaveAvailablePluginsAsObject() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(jsonPath("$.available_plugins", notNullValue()))
                .andExpect(jsonPath("$.available_plugins", aMapWithSize(2)))
                .andExpect(jsonPath("$.available_plugins.face_detector", is("1.0.0")))
                .andExpect(jsonPath("$.available_plugins.face_recognizer", is("1.2.0")));
    }

    @Test
    @DisplayName("Should work without authentication")
    void shouldWorkWithoutAuthentication() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then - No API key header should be required
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should meet all PR requirements using validator utility")
    void shouldMeetAllPrRequirements() throws Exception {
        // Given
        setupMockStatusResponse();

        // When & Then - Validate all PR requirements using the utility
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk())
                .andExpectAll(StatusResponseValidator.validateRequiredFieldsPresent())
                .andExpectAll(StatusResponseValidator.validateStatusResponse());
    }

    @Test
    @DisplayName("Should handle empty similarity coefficients array")
    void shouldHandleEmptySimilarityCoefficients() throws Exception {
        // Given
        FacesStatusResponse mockResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList())
                .setAvailablePlugins(createMockPlugins());

        when(facesApiClient.getStatus()).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(jsonPath("$.similarity_coefficients", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle empty available plugins object")
    void shouldHandleEmptyAvailablePlugins() throws Exception {
        // Given
        FacesStatusResponse mockResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6))
                .setAvailablePlugins(new HashMap<>());

        when(facesApiClient.getStatus()).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(jsonPath("$.available_plugins", aMapWithSize(0)));
    }

    private void setupMockStatusResponse() {
        FacesStatusResponse mockResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6, 0.7, 0.8))
                .setAvailablePlugins(createMockPlugins());

        when(facesApiClient.getStatus()).thenReturn(mockResponse);
    }

    private Map<String, String> createMockPlugins() {
        Map<String, String> plugins = new HashMap<>();
        plugins.put("face_detector", "1.0.0");
        plugins.put("face_recognizer", "1.2.0");
        return plugins;
    }
}