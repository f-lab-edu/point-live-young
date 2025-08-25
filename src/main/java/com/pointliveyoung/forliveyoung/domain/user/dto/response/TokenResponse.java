package com.pointliveyoung.forliveyoung.domain.user.dto.response;


import java.util.Objects;

public record TokenResponse(String accessToken) {
    public TokenResponse {
        Objects.requireNonNull(accessToken, "accessToken은 null일 수 없습니다.");
    }
}
