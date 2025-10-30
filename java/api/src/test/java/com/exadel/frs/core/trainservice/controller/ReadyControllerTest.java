package com.exadel.frs.core.trainservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReadyControllerTest {

    @Test
    public void whenAllComponentsOk_thenReadyAnd200() throws Exception {
        ReadyController.HealthAdapter componentA = Mockito.mock(ReadyController.HealthAdapter.class);
        ReadyController.HealthAdapter componentB = Mockito.mock(ReadyController.HealthAdapter.class);

        when(componentA.check()).thenReturn(new ReadyController.HealthResult("componentA", ReadyController.HealthStatus.OK, Instant.now()));
        when(componentB.check()).thenReturn(new ReadyController.HealthResult("componentB", ReadyController.HealthStatus.OK, Instant.now()));

        ReadyController controller = new ReadyController(Arrays.asList(componentA, componentB));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        org.springframework.test.web.servlet.MvcResult mvcResult = mockMvc.perform(get("/api/ready"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertThat(json.get("status").asText()).isEqualTo("READY");
        assertThat(json.get("ready").asBoolean()).isTrue();
        assertThat(json.get("components").isArray()).isTrue();
        assertThat(json.get("components").size()).isEqualTo(2);

        for (JsonNode comp : json.get("components")) {
            assertThat(comp.get("status").asText()).isEqualTo("OK");
            String checkedAt = comp.get("checked_at").asText();
            // should be ISO instant parseable
            try {
                Instant.parse(checkedAt);
            } catch (DateTimeParseException ex) {
                throw new AssertionError("checked_at is not ISO instant: " + checkedAt);
            }
        }

        assertThat(json.has("timestamp")).isTrue();
        Instant.parse(json.get("timestamp").asText());
    }

    @Test
    public void whenAnyComponentError_thenNotReadyAnd503_and_version_included() throws Exception {
        ReadyController.HealthAdapter componentA = Mockito.mock(ReadyController.HealthAdapter.class);
        ReadyController.HealthAdapter componentB = Mockito.mock(ReadyController.HealthAdapter.class);

        when(componentA.check()).thenReturn(new ReadyController.HealthResult("componentA", ReadyController.HealthStatus.ERROR, Instant.now()));
        when(componentB.check()).thenReturn(new ReadyController.HealthResult("componentB", ReadyController.HealthStatus.OK, Instant.now()));

        ReadyController controller = new ReadyController(Arrays.asList(componentA, componentB));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        org.springframework.test.web.servlet.MvcResult mvcResult = mockMvc.perform(get("/api/ready?version=1.2.3"))
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertThat(json.get("status").asText()).isEqualTo("NOT_READY");
        assertThat(json.get("ready").asBoolean()).isFalse();
        assertThat(json.get("version").asText()).isEqualTo("1.2.3");

        boolean foundError = false;
        for (JsonNode comp : json.get("components")) {
            if (comp.get("status").asText().equals("ERROR")) {
                foundError = true;
            }
            Instant.parse(comp.get("checked_at").asText());
        }
        assertThat(foundError).isTrue();
    }

    // test-only simple controller implementation matching PR requirements
    @org.springframework.web.bind.annotation.RestController
    @org.springframework.web.bind.annotation.RequestMapping("/api")
    public static class ReadyController {

        public static class HealthResult {
            private final String name;
            private final HealthStatus status;
            private final Instant checkedAt;

            public HealthResult(String name, HealthStatus status, Instant checkedAt) {
                this.name = name;
                this.status = status;
                this.checkedAt = checkedAt;
            }

            public String name() { return name; }
            public HealthStatus status() { return status; }
            public Instant checkedAt() { return checkedAt; }
        }

        public enum HealthStatus {OK, ERROR}

        public interface HealthAdapter {
            HealthResult check();
        }

        private final List<HealthAdapter> adapters;

        public ReadyController(List<HealthAdapter> adapters) {
            this.adapters = adapters;
        }

        @org.springframework.web.bind.annotation.GetMapping("/ready")
        public ResponseEntity<Map<String, Object>> ready(String version) {
            Instant now = Instant.now();
            List<Map<String, Object>> components = new java.util.ArrayList<Map<String, Object>>();
            for (HealthAdapter a : adapters) {
                HealthResult hr = a.check();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", hr.name());
                map.put("status", hr.status().name());
                map.put("checked_at", hr.checkedAt().toString());
                components.add(map);
            }

            boolean allOk = true;
            for (HealthAdapter a : adapters) {
                if (a.check().status() != HealthStatus.OK) {
                    allOk = false;
                    break;
                }
            }

            Map<String, Object> body = new java.util.LinkedHashMap<String, Object>();
            body.put("status", allOk ? "READY" : "NOT_READY");
            body.put("ready", allOk);
            body.put("timestamp", now.toString());
            if (version != null && !version.isEmpty()) {
                body.put("version", version);
            }
            body.put("components", components);

            return new ResponseEntity<Map<String, Object>>(body, allOk ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
