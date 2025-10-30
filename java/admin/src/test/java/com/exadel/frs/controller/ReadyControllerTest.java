package com.exadel.frs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReadyControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReadyController controller = new ReadyController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void whenAllComponentsOk_thenReadyAnd200() throws Exception {
        ReadyController controller = new ReadyController();
        controller.registerComponent("db", new TestAdapter(true, Instant.parse("2020-01-01T00:00:00Z")));
        controller.registerComponent("ml", new TestAdapter(true, Instant.parse("2020-01-01T00:00:01Z")));

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/ready").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("READY")))
                .andExpect(jsonPath("$.ok", is(true)))
                .andExpect(jsonPath("$.components.db.status", is("OK")))
                .andExpect(jsonPath("$.components.ml.status", is("OK")));
    }

    @Test
    void whenAnyComponentNotOk_thenNotReadyAnd503() throws Exception {
        ReadyController controller = new ReadyController();
        controller.registerComponent("db", new TestAdapter(false, Instant.parse("2020-01-01T00:00:00Z")));
        controller.registerComponent("ml", new TestAdapter(true, Instant.parse("2020-01-01T00:00:01Z")));

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/ready").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status", is("NOT_READY")))
                .andExpect(jsonPath("$.ok", is(false)))
                .andExpect(jsonPath("$.components.db.status", is("ERROR")))
                .andExpect(jsonPath("$.components.db.checked_at").exists());
    }

    // Simple in-test controller to simulate the readiness endpoint behavior
    public static class ReadyController {
        private final Map<String, ComponentHealthAdapter> adapters = new HashMap<>();

        public void registerComponent(String name, ComponentHealthAdapter adapter) {
            adapters.put(name, adapter);
        }

        public Object ready() {
            Map<String, Object> response = new HashMap<>();
            boolean allOk = true;
            Map<String, Object> components = new HashMap<>();
            for (var entry : adapters.entrySet()) {
                var st = entry.getValue().check();
                Map<String, Object> comp = new HashMap<>();
                comp.put("status", st.isOk() ? "OK" : "ERROR");
                comp.put("checked_at", st.getCheckedAt().toString());
                components.put(entry.getKey(), comp);
                if (!st.isOk()) allOk = false;
            }
            response.put("status", allOk ? "READY" : "NOT_READY");
            response.put("ok", allOk);
            response.put("components", components);
            response.put("timestamp", Instant.now().toString());
            return response;
        }
    }

    // Simple adapter implementation for tests
    public static class TestAdapter implements ComponentHealthAdapter {
        private final boolean ok;
        private final Instant checkedAt;

        public TestAdapter(boolean ok, Instant checkedAt) {
            this.ok = ok;
            this.checkedAt = checkedAt;
        }

        @Override
        public ComponentStatus check() {
            return new ComponentStatus(ok, checkedAt, ok ? "" : "failure");
        }
    }

    public static class ComponentStatus {
        private final boolean ok;
        private final Instant checkedAt;
        private final String message;

        public ComponentStatus(boolean ok, Instant checkedAt, String message) {
            this.ok = ok;
            this.checkedAt = checkedAt;
            this.message = message;
        }

        public boolean isOk() { return ok; }
        public Instant getCheckedAt() { return checkedAt; }
        public String getMessage() { return message; }
    }
}
