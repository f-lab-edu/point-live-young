package com.pointliveyoung.forliveyoung.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointliveyoung.forliveyoung.domain.user.config.SecurityConfig;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.LoginRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.UserCreateRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.response.AuthTokens;
import com.pointliveyoung.forliveyoung.domain.user.service.UserService;
import com.pointliveyoung.forliveyoung.domain.user.token.JwtAuthFilter;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String REFRESH_COOKIE = "refreshToken";

    @Test
    @DisplayName("POST /api/users - 회원가입 201 Created")
    void signUp() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        ReflectionTestUtils.setField(request, "name", "fiat_lux");
        ReflectionTestUtils.setField(request, "email", "test@naver.com");
        ReflectionTestUtils.setField(request, "password", "password123");
        ReflectionTestUtils.setField(request, "birthDate", LocalDate.of(1999, 8, 8));


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).signUp(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/login - 로그인 200 OK + Set-Cookie(refreshToken) + body(accessToken)")
    void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", "test@naver.com");
        ReflectionTestUtils.setField(loginRequest, "password", "password123");

        when(userService.login(any(LoginRequest.class)))
                .thenReturn(new AuthTokens("accessToken", "refreshToken"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(REFRESH_COOKIE + "=refreshToken")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=")));

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/logout - 204 No Content + 쿠키 즉시 만료")
    void logout() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                        .cookie(new Cookie(REFRESH_COOKIE, "refreshToken")))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(REFRESH_COOKIE + "=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")));

        verify(userService, times(1)).logout("refreshToken");
    }

    @Test
    @DisplayName("POST /api/users/refresh - 200 OK + 새 refreshToken 쿠키 + 새 accessToken 바디")
    void refresh() throws Exception {
        when(userService.renewTokens("refreshToken"))
                .thenReturn(new AuthTokens("accessToken", "refreshToken"));

        mockMvc.perform(post("/api/users/refresh")
                        .cookie(new Cookie(REFRESH_COOKIE, "refreshToken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(REFRESH_COOKIE + "=refreshToken")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")));

        verify(userService, times(1)).renewTokens("refreshToken");
    }
}