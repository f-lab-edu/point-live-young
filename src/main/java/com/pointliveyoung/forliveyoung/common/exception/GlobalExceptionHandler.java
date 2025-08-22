package com.pointliveyoung.forliveyoung.common.exception;

import com.pointliveyoung.forliveyoung.common.response.ResponseEntityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(final BusinessException e) {
        return ResponseEntityUtil.toResponseEntity(e.getExceptionCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntityUtil.toResponseEntity(ExceptionCode.INVALID_INPUT_VALUE, detail);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ExceptionResponse> handleArgumentErrors(Exception e) {
        return ResponseEntityUtil.toResponseEntity(ExceptionCode.INVALID_INPUT_VALUE, "요청 파라미터가 올바르지 않습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        return ResponseEntityUtil.toResponseEntity(ExceptionCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }
}
