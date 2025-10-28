package com.exadel.frs.commonservice.sdk.faces.feign.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FacesStatusResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeToJsonWithCorrectFieldNames() throws JsonProcessingException {
        // Given
        FacesStatusResponse response = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6, 0.7, 0.8))
                .setAvailablePlugins(createMockPlugins());

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"status\":\"OK\"")
                       .contains("\"build_version\":\"1.0.0\"")
                       .contains("\"calculator_version\":\"2.1.0\"")
                       .contains("\"similarity_coefficients\":[0.6,0.7,0.8]")
                       .contains("\"available_plugins\":{\"face_detector\":\"1.0.0\",\"face_recognizer\":\"1.2.0\"}");
    }

    @Test
    void shouldDeserializeFromJsonWithCorrectFieldNames() throws JsonProcessingException {
        // Given
        String json = "{\"status\":\"OK\"," +
                     "\"build_version\":\"1.0.0\"," +
                     "\"calculator_version\":\"2.1.0\"," +
                     "\"similarity_coefficients\":[0.6,0.7,0.8]," +
                     "\"available_plugins\":{\"face_detector\":\"1.0.0\",\"face_recognizer\":\"1.2.0\"}}";

        // When
        FacesStatusResponse response = objectMapper.readValue(json, FacesStatusResponse.class);

        // Then
        assertThat(response.getStatus()).isEqualTo("OK");
        assertThat(response.getBuildVersion1()).isEqualTo("1.0.0");
        assertThat(response.getCalculatorVersion()).isEqualTo("2.1.0");
        assertThat(response.getSimilarityCoefficients()).containsExactly(0.6, 0.7, 0.8);
        assertThat(response.getAvailablePlugins())
                .hasSize(2)
                .containsEntry("face_detector", "1.0.0")
                .containsEntry("face_recognizer", "1.2.0");
    }

    @Test
    void shouldHandleEmptySimilarityCoefficients() throws JsonProcessingException {
        // Given
        FacesStatusResponse response = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList())
                .setAvailablePlugins(new HashMap<>());

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"similarity_coefficients\":[]");
    }

    @Test
    void shouldHandleEmptyAvailablePlugins() throws JsonProcessingException {
        // Given
        FacesStatusResponse response = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6))
                .setAvailablePlugins(new HashMap<>());

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"available_plugins\":{}");
    }

    @Test
    void shouldValidateRequiredFieldsArePresent() {
        // Given
        FacesStatusResponse response = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(Arrays.asList(0.6))
                .setAvailablePlugins(createMockPlugins());

        // Then
        assertThat(response.getStatus()).isNotBlank();
        assertThat(response.getBuildVersion1()).isNotBlank();
        assertThat(response.getCalculatorVersion()).isNotBlank();
        assertThat(response.getSimilarityCoefficients()).isNotNull();
        assertThat(response.getAvailablePlugins()).isNotNull();
    }

    @Test
    void shouldHandleNullValuesGracefully() throws JsonProcessingException {
        // Given
        FacesStatusResponse response = new FacesStatusResponse()
                .setStatus("OK")
                .setBuildVersion1("1.0.0")
                .setCalculatorVersion("2.1.0")
                .setSimilarityCoefficients(null)
                .setAvailablePlugins(null);

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then - Should not throw exception and should handle nulls
        assertThat(json).isNotNull();
        assertThat(response.getSimilarityCoefficients()).isNull();
        assertThat(response.getAvailablePlugins()).isNull();
    }

    private Map<String, String> createMockPlugins() {
        Map<String, String> plugins = new HashMap<>();
        plugins.put("face_detector", "1.0.0");
        plugins.put("face_recognizer", "1.2.0");
        return plugins;
    }
}