package com.pointliveyoung.forliveyoung.domain.user.controller;

import com.pointliveyoung.forliveyoung.domain.user.dto.request.LoginRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.UserCreateRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.response.AuthTokens;
import com.pointliveyoung.forliveyoung.domain.user.dto.response.TokenResponse;
import com.pointliveyoung.forliveyoung.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final long REFRESH_MAX_AGE = 7 * 24 * 60 * 60;

    @PostMapping
    public ResponseEntity<Void> signUpUser(@Valid @RequestBody UserCreateRequest request) {
        userService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthTokens tokens = userService.login(loginRequest);

        ResponseCookie cookie = buildRefreshTokenCookie(tokens.refreshToken(), REFRESH_MAX_AGE);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new TokenResponse(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {

        userService.logout(refreshToken);

        ResponseCookie delete = buildRefreshTokenCookie("", 0);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, delete.toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
        AuthTokens tokens = userService.renewTokens(refreshToken);

        ResponseCookie cookie = buildRefreshTokenCookie(tokens.refreshToken(), REFRESH_MAX_AGE);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new TokenResponse(tokens.accessToken()));
    }


    private ResponseCookie buildRefreshTokenCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
