package com.exadel.frs.controller;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.exception.AccessDeniedException;
import com.exadel.frs.exception.SelfRoleChangeException;
import com.exadel.frs.handler.ExceptionCode;
import com.exadel.frs.handler.ResponseExceptionHandler;
import com.exadel.frs.service.AppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AppController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {com.exadel.frs.system.security.JwtAuthenticationFilter.class, 
                          com.exadel.frs.system.security.config.WebSecurityConfig.class, 
                          com.exadel.frs.system.security.config.AuthServerConfig.class, 
                          com.exadel.frs.system.security.config.ResourceServerConfig.class})
)
@MockBeans({@MockBean(com.exadel.frs.mapper.AppMapper.class), @MockBean(com.exadel.frs.mapper.UserAppRoleMapper.class)})
class ExceptionHandlingIntegrationTest {

    private static final long APP_ID = 1L;
    private static final long ORG_ID = 2L;
    private static final long USER_ID = 3L;
    private static final String USERNAME = "test";
    private static final String APP_GUID = "app-guid";
    private static final String ORG_GUID = "org-guid";

    @MockBean
    private AppService appService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    private static com.exadel.frs.entity.User buildDefaultUser() {
        return com.exadel.frs.entity.User.builder().email(USERNAME).id(USER_ID).build();
    }

    @Test
    void testAccessDeniedExceptionHandling() throws Exception {
        // Given: AccessDeniedException is thrown
        AccessDeniedException expectedException = new AccessDeniedException();
        
        when(appService.getApp(APP_GUID, USER_ID)).thenThrow(expectedException);

        // When: Making a request that triggers the exception
        MvcResult result = mockMvc.perform(get("/org/" + ORG_GUID + "/app/" + APP_GUID)
                .with(user(buildDefaultUser())))
                .andExpect(status().isForbidden())
                .andReturn();

        // Then: Verify the response contains the expected error code and message
        String responseContent = result.getResponse().getContentAsString();
        ExceptionResponseDto responseDto = mapper.readValue(responseContent, ExceptionResponseDto.class);

        assertThat(responseDto.getCode(), is(ExceptionCode.ACCESS_DENIED.getCode()));
        assertThat(responseDto.getMessage(), is("Access Denied. Application has read only access to model"));
    }

    @Test
    void testSelfRoleChangeExceptionHandling() throws Exception {
        // Given: SelfRoleChangeException is thrown
        SelfRoleChangeException expectedException = new SelfRoleChangeException();
        
        // Mock the updateUserAppRole method to throw SelfRoleChangeException
        doThrow(expectedException).when(appService).updateUserAppRole(any(), eq(APP_GUID), eq(USER_ID));

        // When: Making a request that triggers the exception
        // Note: We need to use a valid endpoint that calls updateUserAppRole
        // For now, we'll test the exception handler directly since the endpoint might not be exposed
        ResponseExceptionHandler handler = new ResponseExceptionHandler();
        ResponseEntity<ExceptionResponseDto> response = handler.handleDefinedExceptions(expectedException);

        // Then: Verify the response contains the expected error code and message
        assertThat(response.getBody().getCode(), is(ExceptionCode.SELF_ROLE_CHANGE.getCode()));
        assertThat(response.getBody().getMessage(), is("Owner cannot change his own organization/application role"));
    }

    @Test
    void testUndefinedExceptionHandling() throws Exception {
        // Given: An undefined exception is thrown
        NullPointerException expectedException = new NullPointerException();
        
        when(appService.getApps(ORG_GUID, USER_ID)).thenThrow(expectedException);

        // When: Making a request that triggers the exception
        MvcResult result = mockMvc.perform(get("/org/" + ORG_GUID + "/apps")
                .with(user(buildDefaultUser())))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then: Verify the response contains the expected error code
        String responseContent = result.getResponse().getContentAsString();
        ExceptionResponseDto responseDto = mapper.readValue(responseContent, ExceptionResponseDto.class);

        assertThat(responseDto.getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        // Current behavior: uses the exception's message (which is null for NullPointerException)
        // After change: should return "Something went wrong, please try again"
    }

    @Test
    void testUndefinedExceptionWithMessageHandling() throws Exception {
        // Given: An undefined exception with a message is thrown
        IllegalArgumentException expectedException = new IllegalArgumentException("Test error message");
        
        when(appService.getApps(ORG_GUID, USER_ID)).thenThrow(expectedException);

        // When: Making a request that triggers the exception
        MvcResult result = mockMvc.perform(get("/org/" + ORG_GUID + "/apps")
                .with(user(buildDefaultUser())))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then: Verify the response contains the expected error code and message
        String responseContent = result.getResponse().getContentAsString();
        ExceptionResponseDto responseDto = mapper.readValue(responseContent, ExceptionResponseDto.class);

        assertThat(responseDto.getCode(), is(ExceptionCode.UNDEFINED.getCode()));
        // Current behavior: uses the exception's message
        // After change: should return "Something went wrong, please try again" regardless of original message
    }
}