package com.exadel.frs.controller;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import com.exadel.frs.handler.ExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests that verify error responses match PR requirements.
 * These tests will fail with the current implementation but should pass after
 * the production code is updated according to the PR requirements.
 */
@WebMvcTest(value = AppController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {com.exadel.frs.system.security.JwtAuthenticationFilter.class, 
                          com.exadel.frs.system.security.config.WebSecurityConfig.class, 
                          com.exadel.frs.system.security.config.AuthServerConfig.class, 
                          com.exadel.frs.system.security.config.ResourceServerConfig.class})
)
@MockBeans({@MockBean(com.exadel.frs.mapper.AppMapper.class), 
            @MockBean(com.exadel.frs.mapper.UserAppRoleMapper.class)})
class ErrorResponseIntegrationTest {

    private static final long APP_ID = 1L;
    private static final long ORG_ID = 2L;
    private static final long USER_ID = 3L;
    private static final String USERNAME = "test";
    private static final String APP_GUID = "app-guid";
    private static final String ORG_GUID = "org-guid";

    @MockBean
    private com.exadel.frs.service.AppService appService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    private static com.exadel.frs.entity.User buildDefaultUser() {
        return com.exadel.frs.entity.User.builder().email(USERNAME).id(USER_ID).build();
    }

    @Test
    void shouldReturnAccessDeniedErrorWithCorrectCodeAndMessage() throws Exception {
        // Test AccessDeniedException error response
        final AccessDeniedException expectedException = new AccessDeniedException();

        when(appService.getApp(APP_GUID, USER_ID)).thenThrow(expectedException);

        String expectedContent = mapper.writeValueAsString(buildExceptionResponse(expectedException));
        mockMvc.perform(get("/org/" + ORG_GUID + "/app/" + APP_GUID).with(user(buildDefaultUser())))
                .andExpect(status().isForbidden())
                .andExpect(content().string(expectedContent));
    }

    @Test
    void shouldReturnSelfRoleChangeErrorWithUpdatedMessage() throws Exception {
        // Test SelfRoleChangeException error response with updated message
        final SelfRoleChangeException expectedException = new SelfRoleChangeException();

        doThrow(expectedException).when(appService).updateUserAppRole(any(), eq(APP_GUID), eq(USER_ID));

        // This test will fail until the production code is updated
        String expectedContent = mapper.writeValueAsString(buildExceptionResponse(expectedException));
        
        MockHttpServletRequestBuilder request = post("/org/" + ORG_GUID + "/app/" + APP_GUID + "/role")
                .with(csrf())
                .with(user(buildDefaultUser()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));
    }

    @Test
    void shouldReturnDefaultMessageForUndefinedExceptions() throws Exception {
        // Test that undefined exceptions return the default message
        final Exception expectedException = new NullPointerException();

        when(appService.getApps(ORG_GUID, USER_ID)).thenThrow(expectedException);

        // This test will fail until the production code is updated
        String expectedContent = mapper.writeValueAsString(buildUndefinedExceptionResponseWithDefaultMessage());
        mockMvc.perform(get("/org/" + ORG_GUID + "/apps").with(user(buildDefaultUser())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));
    }

    private ExceptionResponseDto buildExceptionResponse(final com.exadel.frs.exception.BasicException ex) {
        return ExceptionResponseDto.builder()
                .code(ex.getExceptionCode().getCode())
                .message(ex.getMessage())
                .build();
    }

    private ExceptionResponseDto buildUndefinedExceptionResponseWithDefaultMessage() {
        return ExceptionResponseDto.builder()
                .code(ExceptionCode.UNDEFINED.getCode())
                .message("Something went wrong, please try again")
                .build();
    }
}