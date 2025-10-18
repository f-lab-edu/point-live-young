package com.pointliveyoung.forliveyoung.domain.order.event;

import java.util.List;

public record PurchaseCompletedEvent(String userEmail,
                                     int quantity,
                                     int totalPrice,
                                     String productName,
                                     List<String> codeList) {
}