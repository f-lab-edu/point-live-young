package com.pointliveyoung.forliveyoung.domain.order.controller;

import com.pointliveyoung.forliveyoung.domain.order.dto.request.PurchaseRequest;
import com.pointliveyoung.forliveyoung.domain.order.dto.response.OrderCancelResponse;
import com.pointliveyoung.forliveyoung.domain.order.dto.response.OrderHistoryResponse;
import com.pointliveyoung.forliveyoung.domain.order.dto.response.PurchaseResponse;
import com.pointliveyoung.forliveyoung.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<PurchaseResponse> purchaseProducts(@AuthenticationPrincipal Integer userId,
                                                             @RequestBody PurchaseRequest request) {
        PurchaseResponse response = orderService.purchaseProducts(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderHistoryResponse>> getMyOrders(@AuthenticationPrincipal Integer userId) {
        List<OrderHistoryResponse> response = orderService.getAllOrders(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<OrderCancelResponse> cancelOrder(@AuthenticationPrincipal Integer userId,
                                                           @PathVariable Integer orderId) {

        OrderCancelResponse response = orderService.cancel(userId, orderId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}
