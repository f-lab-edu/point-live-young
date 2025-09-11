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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private OrderPointUsageRepository orderPointUsageRepository;

    @Mock
    private PointUseService pointUseService;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("purchaseProducts() 성공")
    void purchaseProducts_success() {
        Integer userId = 1;
        User user = mock(User.class);

        Product product = mock(Product.class);
        when(product.getName()).thenReturn("상품A");

        when(userService.getUserById(any(Integer.class))).thenReturn(user);
        when(productService.getById(any(Integer.class))).thenReturn(product);
        when(product.getStock()).thenReturn(5);

        PurchaseRequest request = new PurchaseRequest(100, 2, 2000);

        Order order = Order.create(4000, 2, product, user);
        ReflectionTestUtils.setField(order, "id", 1);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PurchaseResponse response = orderService.purchaseProducts(userId, request);

        assertEquals(4000, response.totalPrice());
        assertEquals(2, response.quantity());
        assertEquals(2, response.orderItemCodeList().size());

        verify(productService, times(1)).decreaseStock(product, 2);
        verify(pointUseService, times(1)).consume(userId, order.getPurchasePrice(), order);
        verify(orderRepository, times(1)).save(any(Order.class));
    }


    @Test
    @DisplayName("purchaseProducts() 실패 - 재고 부족")
    void purchaseProducts_fail() {
        User user = mock(User.class);
        Product product = mock(Product.class);

        when(userService.getUserById(any(Integer.class))).thenReturn(user);
        when(productService.getById(any(Integer.class))).thenReturn(product);
        when(product.getStock()).thenReturn(1);

        assertThrows(IllegalStateException.class,
                () -> orderService.purchaseProducts(1, new PurchaseRequest(100, 1000, 3)));
    }


    @Test
    @DisplayName("getAllOrders() 성공")
    void getAllOrders_success() {
        Integer userId = 1;

        User user = mock(User.class);
        ReflectionTestUtils.setField(user, "id", 1);

        Product product = mock(Product.class);
        ReflectionTestUtils.setField(product, "id", 1);
        when(product.getId()).thenReturn(1);
        when(product.getName()).thenReturn("상품A");


        Order order = mock(Order.class);
        when(order.getId()).thenReturn(100);
        when(order.getStatus()).thenReturn(OrderStatus.COMPLETED);
        when(order.getProduct()).thenReturn(product);
        when(order.getPurchasePrice()).thenReturn(3000);
        LocalDateTime created1 = LocalDateTime.now().minusDays(1);
        when(order.getCreatedAt()).thenReturn(created1);

        when(orderRepository.findByUser_IdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(order));


        OrderItem item1 = mock(OrderItem.class);
        when(item1.getPriceAtPurchase()).thenReturn(1500);
        when(item1.getProductCode()).thenReturn("itemCode");

        when(orderItemRepository.findByOrder_Id(100))
                .thenReturn(List.of(item1));

        List<OrderHistoryResponse> resultList = orderService.getAllOrders(userId);

        assertEquals(1, resultList.size());

        OrderHistoryResponse result = resultList.get(0);
        assertEquals(100, result.orderId());
        assertEquals(OrderStatus.COMPLETED.name(), result.status());
        assertEquals(1, result.productId());
        assertEquals("상품A", result.productName());
        assertEquals(created1, result.createdAt());
        assertEquals(List.of("itemCode"), result.orderItemCodes());

    }

    @Test
    @DisplayName("cancel() 성공 - 활성 되어있는 UserPointLot 만 복원, 만료 UserPointLot는 스킵")
    void cancel_success() {
        Integer userId = 1;
        Integer orderId = 10;

        Order order = mock(Order.class);
        Product product = mock(Product.class);

        when(order.getStatus()).thenReturn(OrderStatus.COMPLETED);
        when(order.isExpired()).thenReturn(false);
        when(order.getProduct()).thenReturn(product);
        when(order.getQuantity()).thenReturn(2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderPointUsage orderPointUsage1 = mock(OrderPointUsage.class);
        UserPointLot lot1 = mock(UserPointLot.class);
        when(orderPointUsage1.getLot()).thenReturn(lot1);
        when(orderPointUsage1.getUsedAmount()).thenReturn(300);
        when(lot1.isExpired(any(LocalDateTime.class))).thenReturn(false);

        OrderPointUsage orderPointUsage2 = mock(OrderPointUsage.class);
        UserPointLot lot2 = mock(UserPointLot.class);
        when(orderPointUsage2.getLot()).thenReturn(lot2);
        when(lot2.isExpired(any(LocalDateTime.class))).thenReturn(true);

        when(orderPointUsageRepository.findByOrder_Id(orderId)).thenReturn(List.of(orderPointUsage1, orderPointUsage2));

        OrderCancelResponse result = orderService.cancel(userId, orderId);

        verify(productService).increaseStock(product, 2);
        verify(lot1, times(1)).cancelPoint(300);
        verify(lot2, never()).cancelPoint(anyInt());
        verify(order, times(1)).changeStatus(OrderStatus.CANCELED);

        assertEquals(result.pointAmount(), 300);
    }

    @Test
    @DisplayName("cancel() 실패 - 이미 취소되었거나 취소불가 상태면 예외")
    void cancel_fail1() {
        int userId = 1;
        int orderId = 222;

        doNothing().when(userService).checkExistUserById(userId);

        Order order = mock(Order.class);
        when(order.getStatus()).thenReturn(OrderStatus.CANCELED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancel(userId, orderId));

        verify(productService, never()).increaseStock(any(), anyInt());
    }

    @Test
    @DisplayName("cancel() 실패 - 환불 가능 기한이 지났으면 expire() 후 예외")
    void cancel_refundExpired_throws() {
        int userId = 1;
        int orderId = 1;

        Order order = mock(Order.class);
        when(order.getStatus()).thenReturn(OrderStatus.COMPLETED);
        when(order.isExpired()).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancel(userId, orderId));

        verify(productService, never()).increaseStock(any(), any(Integer.class));
    }


}