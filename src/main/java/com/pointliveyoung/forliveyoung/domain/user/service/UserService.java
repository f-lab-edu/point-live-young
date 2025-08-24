package com.pointliveyoung.forliveyoung.domain.user.service;

import com.pointliveyoung.forliveyoung.domain.user.token.JwtTokenUtil;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.LoginRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.UserCreateRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.response.TokenResponse;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";


    @Transactional
    public void signUpUser(UserCreateRequest request) {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        String encodePassword = passwordEncoder.encode(request.getPassword());

        userRepository.save(User.of(request.getName(), request.getEmail(), encodePassword, request.getBirthDate()));
    }

    @Transactional
    public TokenResponse loginUser(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUserRole());
        user.setRefreshToken(refreshToken);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new TokenResponse(accessToken);
    }

    @Transactional
    public TokenResponse refreshToken(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);

        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        String userId = jwtTokenUtil.getUserId(refreshToken);

        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));


        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUserRole());

        return new TokenResponse(newAccessToken);
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);

        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        String userId = jwtTokenUtil.getUserId(refreshToken);

        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        user.setRefreshToken(null);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (Objects.isNull(request.getCookies())) {
            throw new IllegalArgumentException("쿠키가 존재하지 않습니다.");
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token 쿠키가 존재하지 않습니다."));
    }


}
