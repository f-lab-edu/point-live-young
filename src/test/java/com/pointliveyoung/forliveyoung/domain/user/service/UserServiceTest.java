package com.pointliveyoung.forliveyoung.domain.user.service;

import com.pointliveyoung.forliveyoung.domain.point.event.PointEvent;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.LoginRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.UserCreateRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.response.AuthTokens;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import com.pointliveyoung.forliveyoung.domain.user.token.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;


    private User user;

    @BeforeEach
    void setUp() {
        user = User.of("test", "test@naver.com", "encodedPassword", LocalDate.of(1999, 8, 8));
        ReflectionTestUtils.setField(user, "id", 1);
    }

    @Test
    @DisplayName("signUp() 성공")
    void signUp_success() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        ReflectionTestUtils.setField(userCreateRequest, "name", user.getName());
        ReflectionTestUtils.setField(userCreateRequest, "email", user.getEmail());
        ReflectionTestUtils.setField(userCreateRequest, "password", "password123");
        ReflectionTestUtils.setField(userCreateRequest, "birthDate", user.getBirthDate());

        when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.signUp(userCreateRequest);

        verify(userRepository, times(1)).existsUserByEmail(any(String.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishEvent(any(PointEvent.class));
    }

    @Test
    @DisplayName("singUp() 실패 - 이미 존재하는 이메일")
    void signUp_fail_1() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        ReflectionTestUtils.setField(userCreateRequest, "name", user.getName());
        ReflectionTestUtils.setField(userCreateRequest, "email", user.getEmail());
        ReflectionTestUtils.setField(userCreateRequest, "password", "password123");
        ReflectionTestUtils.setField(userCreateRequest, "birthDate", user.getBirthDate());

        when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.signUp(userCreateRequest);
        });

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        verify(userRepository, times(1)).existsUserByEmail(any(String.class));
        verify(passwordEncoder, times(0)).encode(any(String.class));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("login() 성공")
    void login_success() {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", user.getEmail());
        ReflectionTestUtils.setField(loginRequest, "password", "password123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateAccessToken(user.getId(), user.getUserRole())).thenReturn("accessToken");
        when(jwtTokenUtil.generateRefreshToken(user.getId(), user.getUserRole())).thenReturn("refreshToken");

        AuthTokens actual = userService.login(loginRequest);

        assertNotNull(actual);
        assertEquals("accessToken", actual.accessToken());
        assertEquals("refreshToken", actual.refreshToken());

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(jwtTokenUtil, times(1)).generateAccessToken(any(Integer.class), any());
        verify(jwtTokenUtil, times(1)).generateRefreshToken(any(Integer.class), any());
    }

    @Test
    @DisplayName("login() 실패 - 존재하지 않는 이메일")
    void login_fail_1() {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", user.getEmail());
        ReflectionTestUtils.setField(loginRequest, "password", "password123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("해당 이메일의 사용자가 존재하지 않습니다.", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(passwordEncoder, times(0)).matches(any(String.class), any(String.class));
        verify(jwtTokenUtil, times(0)).generateAccessToken(any(Integer.class), any());
        verify(jwtTokenUtil, times(0)).generateRefreshToken(any(Integer.class), any());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("login() 실패 - 비밀번호 불일치")
    void login_fail_2() {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", user.getEmail());
        ReflectionTestUtils.setField(loginRequest, "password", "wrongPassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(jwtTokenUtil, times(0)).generateAccessToken(any(Integer.class), any());
        verify(jwtTokenUtil, times(0)).generateRefreshToken(any(Integer.class), any());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("renewTokens() 성공")
    void renewTokens_success() {
        String refreshToken = "refreshToken";
        user.changeRefreshToken(refreshToken);

        when(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn("1");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(jwtTokenUtil.generateAccessToken(user.getId(), user.getUserRole())).thenReturn("newAccessToken");
        when(jwtTokenUtil.generateRefreshToken(user.getId(), user.getUserRole())).thenReturn("refreshToken");

        AuthTokens tokens = userService.renewTokens(refreshToken);

        assertEquals("newAccessToken", tokens.accessToken());
        assertEquals("refreshToken", tokens.refreshToken());
        assertEquals("refreshToken", user.getRefreshToken());

        verify(jwtTokenUtil, times(1)).validateToken(any(String.class));
        verify(jwtTokenUtil, times(1)).getUserId(any(String.class));
        verify(userRepository, times(1)).findById(any(Integer.class));
        verify(jwtTokenUtil, times(1)).generateAccessToken(any(Integer.class), any());
        verify(jwtTokenUtil, times(1)).generateRefreshToken(any(Integer.class), any());
    }

    @Test
    @DisplayName("renewTokens() 실패 - 1. Refresh Token이 null 일 때")
    void renew_fail_1() {
        SecurityException securityException = assertThrows(SecurityException.class, () -> userService.renewTokens(null));
        assertEquals("유효하지 않은 Refresh Token 입니다.", securityException.getMessage());

        verify(jwtTokenUtil, times(0)).validateToken(any(String.class));
        verify(jwtTokenUtil, times(0)).getUserId(any(String.class));
        verify(userRepository, times(0)).findById(any(Integer.class));
        verify(jwtTokenUtil, times(0)).generateAccessToken(any(Integer.class), any());
        verify(jwtTokenUtil, times(0)).generateRefreshToken(any(Integer.class), any());
    }

    @Test
    @DisplayName("renewTokens() 실패 - 2. Refresh Token이 유효하지 않을 때")
    void renew_fail_2() {
        String refreshToken = "refreshToken";
        when(jwtTokenUtil.validateToken(refreshToken)).thenReturn(false);

        SecurityException securityException = assertThrows(SecurityException.class, () -> userService.renewTokens(refreshToken));
        assertEquals("유효하지 않은 Refresh Token 입니다.", securityException.getMessage());
    }

    @Test
    @DisplayName("renewTokens() 실패 - 3. 사용자가 존재하지 않을 때")
    void renew_fail_3() {
        String refreshToken = "refreshToken";
        when(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true);

        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn("1");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class, () -> userService.renewTokens(refreshToken));
        assertEquals("해당 사용자가 존재하지 않습니다.", noSuchElementException.getMessage());
    }

    @Test
    @DisplayName("logout() 성공")
    void logout_success() {
        String refreshToken = "refreshToken";
        user.changeRefreshToken(refreshToken);

        when(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn("1");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.logout(refreshToken);

        assertNull(user.getRefreshToken());
    }

    @Test
    @DisplayName("logout() 실패 - 1. Refresh Token이 null 일 때")
    void logout_fail_1() {
        userService.logout(null);

        verify(jwtTokenUtil, times(0)).validateToken(any(String.class));
        verify(jwtTokenUtil, times(0)).getUserId(any(String.class));
        verify(userRepository, times(0)).findById(any(Integer.class));
    }

    @Test
    @DisplayName("logout() 실패 - 2. Refresh Token이 유효하지 않을 때")
    void logout_fail_2() {
        String refreshToken = "refreshToken";

        when(jwtTokenUtil.validateToken(refreshToken)).thenReturn(false);
        userService.logout(refreshToken);

        verify(jwtTokenUtil, times(1)).validateToken(any(String.class));
        verify(jwtTokenUtil, times(0)).getUserId(any(String.class));
        verify(userRepository, times(0)).findById(any(Integer.class));
    }

    @Test
    @DisplayName("logout() 실패 - 3. 사용자가 존재하지 않을 때")
    void logout_fail_3() {
        String refreshToken = "refreshToken";

        when(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenUtil.getUserId(refreshToken)).thenReturn("1");
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class, () -> userService.logout(refreshToken));

        assertEquals("해당 사용자가 존재하지 않습니다.", noSuchElementException.getMessage());
    }


}