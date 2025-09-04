package com.pointliveyoung.forliveyoung.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "`order`")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, name = "purchase_price")
    private Integer purchasePrice;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderStatus status;

    @JsonFormat
    @Column(nullable = false, name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "expired_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Order(Integer purchasePrice, Integer quantity, Product product, User user) {
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.product = product;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusDays(1);
        this.status = OrderStatus.COMPLETED;
    }

    public static Order create(Integer purchasePrice, Integer quantity, Product product, User user) {
        return new Order(purchasePrice, quantity, product, user);
    }

    public void changeStatus(OrderStatus status) {
        if (Objects.isNull(status)) {
            throw new IllegalArgumentException("status 는 null 이면 안됩니다.");
        }
        this.status = status;
    }


}
