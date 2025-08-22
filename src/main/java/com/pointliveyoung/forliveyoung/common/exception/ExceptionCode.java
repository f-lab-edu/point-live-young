package com.pointliveyoung.forliveyoung.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-001", "Validation failed for the input."),
    PARAMETER_NULL(HttpStatus.BAD_REQUEST, "COMMON-002", "A required parameter is null."),
    PARAMETER_ID_VALUE(HttpStatus.BAD_REQUEST, "COMMON-003", "ID value must be greater than zero."),
    NO_ENDPOINT(HttpStatus.NOT_FOUND, "COMMON-004", "The requested endpoint does not exist."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-005", "The HTTP method is not allowed."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-006", "An internal server error has occurred.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
