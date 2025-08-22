package com.pointliveyoung.forliveyoung.common.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Builder
public record ExceptionResponse(HttpStatus status, String code, String message, String detailMessage) {
    public ExceptionResponse {
        Objects.requireNonNull(status, "ExceptionResponse status cannot be null");
        Objects.requireNonNull(code, "ExceptionResponse code cannot be null");
        Objects.requireNonNull(message, "ExceptionResponse message cannot be null");
    }
}