package com.pointliveyoung.forliveyoung.domain.order.service;

import com.pointliveyoung.forliveyoung.domain.order.dto.request.PurchaseRequest;
import com.pointliveyoung.forliveyoung.domain.order.dto.response.OrderCancelResponse;
import com.pointliveyoung.forliveyoung.domain.order.dto.response.OrderHistoryResponse;
import com.pointliveyoung.forliveyoung.domain.order.dto.response.PurchaseResponse;
import com.pointliveyoung.forliveyoung.domain.order.entity.Order;
import com.pointliveyoung.forliveyoung.domain.order.entity.OrderItem;
import com.pointliveyoung.forliveyoung.domain.order.entity.OrderPointUsage;
import com.pointliveyoung.forliveyoung.domain.order.entity.OrderStatus;
import com.pointliveyoung.forliveyoung.domain.order.repository.OrderItemRepository;
import com.pointliveyoung.forliveyoung.domain.order.repository.OrderPointUsageRepository;
import com.pointliveyoung.forliveyoung.domain.order.repository.OrderRepository;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.service.PointUseService;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import com.pointliveyoung.forliveyoung.domain.product.service.ProductService;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final UserService userService;
    private final OrderPointUsageRepository orderPointUsageRepository;
    private final PointUseService pointUseService;

    @Transactional
    public PurchaseResponse purchaseProducts(Integer userId, PurchaseRequest request) {
        User user = userService.getUserById(userId);

        Product product = productService.getById(request.productId());

        if (product.getStock() < request.quantity()) {
            throw new IllegalStateException("재고 부족");
        }

        int totalPrice = request.unitPrice() * request.quantity();

        productService.decreaseStock(product, request.quantity());

        Order order = Order.create(totalPrice, request.quantity(), product, user);
        Order savedOrder = orderRepository.save(order);

        pointUseService.consume(userId, totalPrice, savedOrder);

        List<OrderItem> orderItemList =
                IntStream.range(0, request.quantity())
                        .mapToObj(i -> {
                            String orderItemCode = generateCode(product.getName());

                            return OrderItem.create(orderItemCode, request.unitPrice(), savedOrder);
                        }).toList();

        orderItemRepository.saveAll(orderItemList);

        return new PurchaseResponse(
                savedOrder.getId(),
                savedOrder.getQuantity(),
                savedOrder.getPurchasePrice(),
                orderItemList.stream()
                        .map(OrderItem::getProductCode)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getAllOrders(Integer userId) {
        userService.checkExistUserById(userId);

        List<Order> orderList = orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        return orderList.stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(order.getId());
                    int quantity = orderItems.size();

                    int unitPrice = orderItems.isEmpty() ? 0 : orderItems.getFirst().getPriceAtPurchase();
                    List<String> orderItemCodes = orderItems.stream().map(OrderItem::getProductCode).toList();

                    return new OrderHistoryResponse(
                            order.getId(),
                            order.getStatus().name(),
                            order.getProduct().getId(),
                            order.getProduct().getName(),
                            quantity,
                            unitPrice,
                            order.getPurchasePrice(),
                            order.getCreatedAt(),
                            orderItemCodes
                    );
                })
                .toList();
    }

    @Transactional
    public OrderCancelResponse cancel(Integer userId, Integer orderId) {
        userService.checkExistUserById(userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("해당 주문이 없습니다."));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 취소되었거나 취소 불가 상태입니다.");
        }

        if (order.isExpired()) {
            order.expire();
            throw new IllegalStateException("환불 가능 기한이 지났습니다.");
        }
        productService.increaseStock(order.getProduct(), order.getQuantity());

        List<OrderPointUsage> usageList = orderPointUsageRepository.findByOrder_Id(orderId);
        int totalCancelPoint = 0;
        for (OrderPointUsage orderPointUsage : usageList) {
            UserPointLot userPointLot = orderPointUsage.getLot();

            boolean isExpired = userPointLot.isExpired(LocalDateTime.now());

            if (!isExpired) {
                Integer usedAmount = orderPointUsage.getUsedAmount();
                userPointLot.cancelPoint(usedAmount);
                totalCancelPoint += usedAmount;
            }
        }

        order.changeStatus(OrderStatus.CANCELED);

        return new OrderCancelResponse(totalCancelPoint);
    }

    private String generateCode(String productName) {
        StringBuilder sb = new StringBuilder();
        sb.append(productName)
                .append(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());

        return sb.toString();
    }


}
