package com.pointliveyoung.forliveyoung.domain.order.dto.response;

import java.util.List;

public record PurchaseResponse(Integer orderId,
                               Integer quantity,
                               Integer totalPrice,
                               List<String> orderItemCodeList) {
}
