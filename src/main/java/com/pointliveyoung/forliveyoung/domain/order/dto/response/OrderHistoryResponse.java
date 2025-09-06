package com.pointliveyoung.forliveyoung.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;


public record OrderHistoryResponse(
        Integer orderId,
        String status,
        Integer productId,
        String productName,
        Integer quantity,
        Integer unitPrice,
        Integer totalPrice,
        LocalDateTime createdAt,
        List<String> orderItemCodes) {

}
