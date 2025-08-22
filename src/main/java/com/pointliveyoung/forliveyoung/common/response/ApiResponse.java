package com.pointliveyoung.forliveyoung.common.response;


import java.util.Objects;

public record ApiResponse<T>(String message, T data) {
    public ApiResponse {
        Objects.requireNonNull(message, "ApiResponse message cannot be null");
    }
}
