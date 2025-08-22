package com.pointliveyoung.forliveyoung.common.response;

import com.pointliveyoung.forliveyoung.common.exception.ExceptionCode;
import com.pointliveyoung.forliveyoung.common.exception.ExceptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ResponseEntityUtilTest {

    @Test
    @DisplayName("toResponseEntity 메서드의 성공 테스트 - 파라미터 : HttpStatus, ApiResponse")
    void toResponseEntity_withHttpStatusAndApiResponse() {
        ApiResponse<String> bodyExpected = new ApiResponse<>("api success", "data");
        HttpStatus httpStatusExpected = HttpStatus.OK;

        ResponseEntity<ApiResponse<?>> actual = ResponseEntityUtil.toResponseEntity(httpStatusExpected, bodyExpected);

        assertNotNull(actual);
        assertNotNull(actual.getBody());
        assertEquals(httpStatusExpected, actual.getStatusCode());
        assertEquals(bodyExpected.message(), actual.getBody().message());
        assertEquals(bodyExpected.data(), actual.getBody().data());
    }

    @Test
    @DisplayName("toResponseEntity 메서드의 파라미터 검증 테스트 - 파라미터 : HttpStatus, ApiResponse ->null 값")
    void toResponseEntity_withNullHttpStatusOrApiResponse() {
        ApiResponse<String> bodyExpected = new ApiResponse<>("api success", "data");
        HttpStatus httpStatusExpected = HttpStatus.OK;

        assertThrows(IllegalArgumentException.class, () -> ResponseEntityUtil.toResponseEntity(null, bodyExpected));
        assertThrows(IllegalArgumentException.class, () -> ResponseEntityUtil.toResponseEntity(httpStatusExpected, null));
    }

    @Test
    @DisplayName("toResponseEntity 메서드의 성공 테스트 - 파라미터 : ExceptionCode")
    void toResponseEntity_withExceptionCode() {
        ExceptionCode exceptionCode = ExceptionCode.INTERNAL_SERVER_ERROR;

        ResponseEntity<ExceptionResponse> actual = ResponseEntityUtil.toResponseEntity(exceptionCode);

        assertNotNull(actual);
        assertNotNull(actual.getBody());
        assertEquals(exceptionCode.getHttpStatus(), actual.getStatusCode());
        assertEquals(exceptionCode.getCode(), actual.getBody().code());
        assertEquals(exceptionCode.getMessage(), actual.getBody().message());
    }

    @Test
    @DisplayName("toResponseEntity 메서드의 파라미터 검증 테스트 - 파라미터 : ExceptionCode -> null 값")
    void toResponseEntity_withNullExceptionCode() {
        assertThrows(IllegalArgumentException.class, () -> ResponseEntityUtil.toResponseEntity(null));
    }

    @Test
    @DisplayName("toResponseEntity 메서드의 성공 테스트 - 파라미터 : ExceptionCode, String")
    void toResponseEntity_withExceptionCodeAndDetailMessage() {
        ExceptionCode exceptionCode = ExceptionCode.INVALID_INPUT_VALUE;
        String detailMessage = "Invalid input provided";

        ResponseEntity<ExceptionResponse> actual = ResponseEntityUtil.toResponseEntity(exceptionCode, detailMessage);

        assertNotNull(actual);
        assertNotNull(actual.getBody());
        assertEquals(exceptionCode.getHttpStatus(), actual.getStatusCode());
        assertEquals(exceptionCode.getCode(), actual.getBody().code());
        assertEquals(exceptionCode.getMessage(), actual.getBody().message());
        assertEquals(detailMessage, actual.getBody().detailMessage());
    }

    @Test
    @DisplayName("toResponseEntity 메서드의 파라미터 검증 테스트 - 파라미터 : ExceptionCode, String -> null 값")
    void toResponseEntity_withNullExceptionCodeOrDetailMessage() {
        ExceptionCode exceptionCode = ExceptionCode.INVALID_INPUT_VALUE;
        String detailMessage = "Invalid input provided";
        assertThrows(IllegalArgumentException.class, () -> ResponseEntityUtil.toResponseEntity(null, detailMessage));
        assertThrows(IllegalArgumentException.class, () -> ResponseEntityUtil.toResponseEntity(exceptionCode, null));
    }
}