package com.pointliveyoung.forliveyoung.domain.user.dto.response;

public record AuthTokens(String accessToken, String refreshToken) {
    public AuthTokens {
        if (accessToken == null || refreshToken == null) {
            throw new IllegalArgumentException("AccessToken and RefreshToken cannot be null");
        }
    }
}