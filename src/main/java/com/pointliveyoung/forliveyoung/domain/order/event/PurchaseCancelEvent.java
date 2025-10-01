package com.pointliveyoung.forliveyoung.domain.order.event;

public record PurchaseCancelEvent(String userEmail,
                                  int quantity,
                                  int totalCancelPrice,
                                  String productName) {
}
