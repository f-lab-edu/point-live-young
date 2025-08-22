package com.pointliveyoung.forliveyoung.common.response;

import com.pointliveyoung.forliveyoung.common.exception.ExceptionCode;
import com.pointliveyoung.forliveyoung.common.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public final class ResponseEntityUtil {

    public static ResponseEntity<ApiResponse<?>> toResponseEntity(final HttpStatus httpStatus, final ApiResponse<?> apiResponse) {
        if (Objects.isNull(httpStatus) || Objects.isNull(apiResponse)) {
            throw new IllegalArgumentException("ResponseEntityUtil - HttpStatus and ApiResponse cannot be null");
        }
        return ResponseEntity
                .status(httpStatus)
                .body(apiResponse);
    }

    public static ResponseEntity<ExceptionResponse> toResponseEntity(final ExceptionCode exceptionCode) {
        if (Objects.isNull(exceptionCode)) {
            throw new IllegalArgumentException("ResponseEntityUtil - ExceptionCode cannot be null");
        }
        return ResponseEntity
                .status(exceptionCode.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .status(exceptionCode.getHttpStatus())
                        .code(exceptionCode.getCode())
                        .message(exceptionCode.getMessage())
                        .build());
    }

    public static ResponseEntity<ExceptionResponse> toResponseEntity(final ExceptionCode exceptionCode, final String detailMessage) {
        if (Objects.isNull(exceptionCode) || Objects.isNull(detailMessage)) {
            throw new IllegalArgumentException("ResponseEntityUtil - ExceptionCode cannot be null");
        }
        return ResponseEntity
                .status(exceptionCode.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .status(exceptionCode.getHttpStatus())
                        .code(exceptionCode.getCode())
                        .message(exceptionCode.getMessage())
                        .detailMessage(detailMessage)
                        .build());
    }


    private ResponseEntityUtil() {
    }
}
