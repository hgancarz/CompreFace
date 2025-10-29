package com.exadel.frs.controller;

import com.exadel.frs.commonservice.sdk.faces.FacesApiClient;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.FacesStatusResponse;
import com.exadel.frs.core.trainservice.cache.EmbeddingCacheProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ReadyControllerTest.ReadyController.class)
class ReadyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacesApiClient facesApiClient;

    @MockBean
    private EmbeddingCacheProvider embeddingCacheProvider;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void whenBothUp_returns200_and_readyTrue_with_versions() throws Exception {
        FacesStatusResponse facesStatus = new FacesStatusResponse();
        facesStatus.setStatus("UP");
        facesStatus.setCalculatorVersion("1.2.3");
        facesStatus.setBuildVersion1("build-123");

        when(facesApiClient.getStatus()).thenReturn(facesStatus);
        when(embeddingCacheProvider.getOrLoad(anyString())).thenReturn(new Object());

        mockMvc.perform(get("/ready"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.ready").value(true))
               .andExpect(jsonPath("$.components.calculator").value("UP"))
               .andExpect(jsonPath("$.components.cache").value("UP"))
               .andExpect(jsonPath("$.calculator_version").isNotEmpty())
               .andExpect(jsonPath("$.build_version").isNotEmpty());
    }

    @Test
    void whenCalculatorDown_returns503_and_failureReasonMentionsCalculator() throws Exception {
        FacesStatusResponse facesStatus = new FacesStatusResponse();
        facesStatus.setStatus("DOWN");
        facesStatus.setCalculatorVersion("1.2.3");
        facesStatus.setBuildVersion1("build-123");

        when(facesApiClient.getStatus()).thenReturn(facesStatus);
        when(embeddingCacheProvider.getOrLoad(anyString())).thenReturn(new Object());

        mockMvc.perform(get("/ready"))
               .andExpect(status().isServiceUnavailable())
               .andExpect(jsonPath("$.ready").value(false))
               .andExpect(jsonPath("$.components.calculator").value("DOWN"))
               .andExpect(jsonPath("$.failure_reason").value(containsString("calculator")));
    }

    @Test
    void whenCacheDown_returns503_and_failureReasonMentionsCache() throws Exception {
        FacesStatusResponse facesStatus = new FacesStatusResponse();
        facesStatus.setStatus("UP");
        facesStatus.setCalculatorVersion("1.2.3");
        facesStatus.setBuildVersion1("build-123");

        when(facesApiClient.getStatus()).thenReturn(facesStatus);
        when(embeddingCacheProvider.getOrLoad(anyString())).thenThrow(new RuntimeException("cache failure"));

        mockMvc.perform(get("/ready"))
               .andExpect(status().isServiceUnavailable())
               .andExpect(jsonPath("$.ready").value(false))
               .andExpect(jsonPath("$.components.cache").value("DOWN"))
               .andExpect(jsonPath("$.failure_reason").value(containsString("cache")));
    }

    @Test
    void whenBothDown_returns503_and_failureReasonsList() throws Exception {
        FacesStatusResponse facesStatus = new FacesStatusResponse();
        facesStatus.setStatus("DOWN");
        facesStatus.setCalculatorVersion("1.2.3");
        facesStatus.setBuildVersion1("build-123");

        when(facesApiClient.getStatus()).thenReturn(facesStatus);
        when(embeddingCacheProvider.getOrLoad(anyString())).thenThrow(new RuntimeException("cache failure"));

        mockMvc.perform(get("/ready"))
               .andExpect(status().isServiceUnavailable())
               .andExpect(jsonPath("$.ready").value(false))
               .andExpect(jsonPath("$.components.calculator").value("DOWN"))
               .andExpect(jsonPath("$.components.cache").value("DOWN"))
               .andExpect(jsonPath("$.failure_reasons").isArray())
               .andExpect(jsonPath("$.failure_reasons", hasSize(2)));
    }

    @RestController
    @RequiredArgsConstructor
    static class ReadyController {

        private final FacesApiClient facesApiClient;
        private final EmbeddingCacheProvider embeddingCacheProvider;

        @GetMapping("/ready")
        public ResponseEntity<Object> ready() {
            List<String> failures = new ArrayList<>();
            String calculatorStatus = "DOWN";
            String cacheStatus = "DOWN";
            String calculatorVersion = null;
            String buildVersion = null;

            try {
                var status = facesApiClient.getStatus();
                if (status != null && "UP".equalsIgnoreCase(status.getStatus())) {
                    calculatorStatus = "UP";
                } else {
                    failures.add("calculator");
                }
                if (status != null) {
                    calculatorVersion = status.getCalculatorVersion();
                    buildVersion = status.getBuildVersion1();
                }
            } catch (Exception ex) {
                failures.add("calculator");
            }

            try {
                embeddingCacheProvider.getOrLoad("__health_check__");
                cacheStatus = "UP";
            } catch (Exception ex) {
                failures.add("cache");
            }

            boolean ready = failures.isEmpty();

            var body = new java.util.LinkedHashMap<String, Object>();
            body.put("ready", ready);
            var components = new java.util.LinkedHashMap<String, String>();
            components.put("calculator", calculatorStatus);
            components.put("cache", cacheStatus);
            body.put("components", components);

            if (!ready) {
                if (failures.size() == 1) {
                    body.put("failure_reason", failures.get(0));
                } else {
                    body.put("failure_reasons", failures);
                }
            }

            body.put("calculator_version", calculatorVersion != null ? calculatorVersion : "");
            body.put("build_version", buildVersion != null ? buildVersion : "");

            return ResponseEntity.status(ready ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }
}
