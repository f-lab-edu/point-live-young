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
import com.pointliveyoung.forliveyoung.domain.point.entity.Status;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import com.pointliveyoung.forliveyoung.domain.product.repository.ProductRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final OrderPointUsageRepository orderPointUsageRepository;

    @Transactional
    public PurchaseResponse purchaseProducts(Integer userId, PurchaseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

        if (product.getStock() < request.quantity()) {
            throw new IllegalStateException("재고 부족");
        }

        int totalPrice = request.unitPrice() * request.quantity();

        product.decreaseStock(request.quantity());

        Order order = Order.create(totalPrice, request.quantity(), product, user);
        Order saveOrder = orderRepository.save(order);

        List<OrderPointUsage> orderPointUsages = dockPoint(user.getId(), totalPrice, order);

        orderPointUsageRepository.saveAll(orderPointUsages);

        List<OrderItem> orderItemList = new ArrayList<>();

        String productName = product.getName();

        LocalDate orderCreateDate = saveOrder.getCreatedAt().toLocalDate();

        for (int i = 0; i < request.quantity(); i++) {
            String orderItemCode = generateCode(productName, orderCreateDate);
            orderItemList.add(OrderItem.create(orderItemCode, request.unitPrice(), saveOrder));
        }

        orderItemRepository.saveAll(orderItemList);

        return new PurchaseResponse(
                saveOrder.getId(),
                saveOrder.getQuantity(),
                saveOrder.getPurchasePrice(),
                orderItemList.stream()
                        .map(OrderItem::getProductCode)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getAllOrders(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        List<Order> orderList = orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        List<OrderHistoryResponse> result = new ArrayList<>();

        for (Order order : orderList) {
            List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(order.getId());

            int quantity = orderItems.size();

            int unitPrice = orderItems.isEmpty() ? 0 : orderItems.getFirst().getPriceAtPurchase();

            List<String> orderItemCodes = orderItems.stream().map(OrderItem::getProductCode).toList();

            result.add(new OrderHistoryResponse(
                    order.getId(),
                    order.getStatus().name(),
                    order.getProduct().getId(),
                    order.getProduct().getName(),
                    quantity,
                    unitPrice,
                    order.getPurchasePrice(),
                    order.getCreatedAt(),
                    orderItemCodes
            ));

        }
        return result;
    }

    @Transactional
    public OrderCancelResponse cancel(Integer userId, Integer orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("해당 주문이 없습니다."));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 취소되었거나 취소 불가 상태입니다.");
        }

        if (LocalDateTime.now().isAfter(order.getExpiredAt())) {
            order.changeStatus(OrderStatus.EXPIRED);
            throw new IllegalStateException("환불 가능 기한이 지났습니다.");
        }

        Integer quantity = order.getQuantity();

        if (quantity <= 0) {
            quantity = orderItemRepository.findByOrder_Id(orderId).size();
        }

        order.getProduct().increaseStock(quantity);

        List<OrderPointUsage> usageList = orderPointUsageRepository.findByOrder_Id(orderId);
        int totalCancelPoint = 0;
        for (OrderPointUsage orderPointUsage : usageList) {
            UserPointLot userPointLot = orderPointUsage.getLot();

            boolean isExpired = Objects.nonNull(userPointLot.getExpirationAt()) &&
                    LocalDateTime.now().isAfter(userPointLot.getExpirationAt()) ||
                    userPointLot.getStatus() == Status.EXPIRED;

            if (!isExpired) {
                Integer usedAmount = orderPointUsage.getUsedAmount();
                userPointLot.cancelPoint(usedAmount);
                totalCancelPoint += usedAmount;
            }

        }

        order.changeStatus(OrderStatus.CANCELED);

        return new OrderCancelResponse(totalCancelPoint);
    }


    private List<OrderPointUsage> dockPoint(Integer userId, int requireTotalPrice, Order order) {
        List<UserPointLot> userPointLotList = userPointRepository.findActivePointByUser(userId);

        int remainPrice = requireTotalPrice;

        List<OrderPointUsage> usageList = new ArrayList<>();

        for (UserPointLot userPointLot : userPointLotList) {
            if (remainPrice <= 0) {
                break;
            }

            int used = userPointLot.dockBalance(remainPrice);

            if (used > 0) {
                usageList.add(OrderPointUsage.create(order, userPointLot, used));
                remainPrice -= used;
            }
        }

        if (remainPrice > 0) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        return usageList;
    }

    private String generateCode(String productName, LocalDate localDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(productName)
                .append(localDate.toString())
                .append(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());

        return sb.toString();
    }


}
