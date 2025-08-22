package com.pointliveyoung.forliveyoung.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private final ExceptionCode exceptionCode;
}
