package com.exadel.frs.controller;

import com.exadel.frs.dto.ExceptionResponseDto;
import com.exadel.frs.entity.App;
import com.exadel.frs.entity.User;
import com.exadel.frs.exception.AppNotFoundException;
import com.exadel.frs.exception.BasicException;
import com.exadel.frs.exception.EmptyRequiredFieldException;
import com.exadel.frs.handler.ExceptionCode;
import com.exadel.frs.mapper.AppMapper;
import com.exadel.frs.mapper.UserAppRoleMapper;
import com.exadel.frs.service.AppService;
import com.exadel.frs.system.security.JwtAuthenticationFilter;
import com.exadel.frs.system.security.config.AuthServerConfig;
import com.exadel.frs.system.security.config.ResourceServerConfig;
import com.exadel.frs.system.security.config.WebSecurityConfig;
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

@WebMvcTest(value = AppController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, WebSecurityConfig.class, AuthServerConfig.class, ResourceServerConfig.class})
)
@MockBeans({@MockBean(AppMapper.class), @MockBean(UserAppRoleMapper.class)})
class AppControllerTest {

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

    private static User buildDefaultUser() {
        return User.builder().email(USERNAME).id(USER_ID).build();
    }

    @Test
    public void shouldReturnMessageAndCodeWhenAppNotFoundExceptionThrown() throws Exception {
        final BasicException expectedException = new AppNotFoundException(APP_GUID);

        when(appService.getApp(APP_GUID, USER_ID)).thenThrow(expectedException);

        String expectedContent = mapper.writeValueAsString(buildExceptionResponse(expectedException));
        mockMvc.perform(get("/org/" + ORG_GUID + "/app/" + APP_GUID).with(user(buildDefaultUser())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedContent));
    }

    @Test
    public void shouldReturnMessageAndCodeWhenUnexpectedExceptionThrown() throws Exception {
        final Exception expectedException = new NullPointerException("test");

        when(appService.getApps(ORG_GUID, USER_ID)).thenThrow(expectedException);

        String expectedContent = mapper.writeValueAsString(buildUndefinedExceptionResponse());
        mockMvc.perform(get("/org/" + ORG_GUID + "/apps").with(user(buildDefaultUser())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));
    }

    @Test
    public void shouldReturnMessageAndCodeWhenAppNameIsMissing() throws Exception {
        final BasicException expectedException = new EmptyRequiredFieldException("name");

        doThrow(expectedException).when(appService).createApp(any(), eq(ORG_GUID), eq(USER_ID));

        MockHttpServletRequestBuilder request = post("/org/" + ORG_GUID + "/app")
                .with(csrf())
                .with(user(buildDefaultUser()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(App.builder().id(APP_ID).build()));

        String expectedContent = mapper.writeValueAsString(buildExceptionResponse(expectedException));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));
    }

    private ExceptionResponseDto buildExceptionResponse(final BasicException ex) {
        return ExceptionResponseDto.builder()
                .code(ex.getExceptionCode().getCode())
                .message(ex.getMessage())
                .build();
    }

    private ExceptionResponseDto buildUndefinedExceptionResponse() {
        return ExceptionResponseDto.builder()
                .code(ExceptionCode.UNDEFINED.getCode())
                .message("Something went wrong, please try again")
                .build();
    }
}