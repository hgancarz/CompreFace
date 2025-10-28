package com.exadel.frs.core.trainservice.controller;

import com.exadel.frs.commonservice.sdk.faces.FacesApiClient;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.FacesStatusResponse;
import com.exadel.frs.core.trainservice.EmbeddedPostgreSQLTest;
import com.exadel.frs.core.trainservice.config.IntegrationTest;
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

@IntegrationTest
@AutoConfigureMockMvc
class StatusControllerTest extends EmbeddedPostgreSQLTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FacesApiClient facesApiClient;

    @Test
    void shouldReturnStatusWithAllRequiredFields() throws Exception {
        // Given
        FacesStatusResponse mockStatusResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6, 0.7, 0.8))
                .setAvailablePlugins(createMockPlugins());

        when(facesApiClient.getStatus()).thenReturn(mockStatusResponse);

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.build_version", is("1.0.0")))
                .andExpect(jsonPath("$.calculator_version", is("2.1.0")))
                .andExpect(jsonPath("$.similarity_coefficients", hasSize(3)))
                .andExpect(jsonPath("$.similarity_coefficients[0]", is(0.6)))
                .andExpect(jsonPath("$.similarity_coefficients[1]", is(0.7)))
                .andExpect(jsonPath("$.similarity_coefficients[2]", is(0.8)))
                .andExpect(jsonPath("$.available_plugins", aMapWithSize(2)))
                .andExpect(jsonPath("$.available_plugins.face_detector", is("1.0.0")))
                .andExpect(jsonPath("$.available_plugins.face_recognizer", is("1.2.0")));
    }

    @Test
    void shouldReturnStatusWithNonEmptyVersionFields() throws Exception {
        // Given
        FacesStatusResponse mockStatusResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6))
                .setAvailablePlugins(new HashMap<>());

        when(facesApiClient.getStatus()).thenReturn(mockStatusResponse);

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.build_version", not(emptyString())))
                .andExpect(jsonPath("$.calculator_version", not(emptyString())));
    }

    @Test
    void shouldReturnStatusWithSimilarityCoefficientsPresent() throws Exception {
        // Given
        FacesStatusResponse mockStatusResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6, 0.7))
                .setAvailablePlugins(new HashMap<>());

        when(facesApiClient.getStatus()).thenReturn(mockStatusResponse);

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.similarity_coefficients", notNullValue()))
                .andExpect(jsonPath("$.similarity_coefficients", hasSize(2)));
    }

    @Test
    void shouldReturnStatusWithAvailablePluginsPresent() throws Exception {
        // Given
        FacesStatusResponse mockStatusResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6))
                .setAvailablePlugins(createMockPlugins());

        when(facesApiClient.getStatus()).thenReturn(mockStatusResponse);

        // When & Then
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available_plugins", notNullValue()))
                .andExpect(jsonPath("$.available_plugins", aMapWithSize(2)));
    }

    @Test
    void shouldReturnStatusWithoutAuthentication() throws Exception {
        // Given
        FacesStatusResponse mockStatusResponse = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6))
                .setAvailablePlugins(new HashMap<>());

        when(facesApiClient.getStatus()).thenReturn(mockStatusResponse);

        // When & Then - Should work without API key authentication
        mockMvc.perform(get(API_V1 + "/status"))
                .andExpect(status().isOk());
    }

    private Map<String, String> createMockPlugins() {
        Map<String, String> plugins = new HashMap<>();
        plugins.put("face_detector", "1.0.0");
        plugins.put("face_recognizer", "1.2.0");
        return plugins;
    }
}