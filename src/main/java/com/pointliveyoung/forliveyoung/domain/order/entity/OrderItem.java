package com.pointliveyoung.forliveyoung.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, name = "product_code", length = 200, unique = true)
    private String productCode;

    @Column(nullable = false, name = "price_at_purchase")
    private Integer priceAtPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private OrderItem(String productCode, Integer priceAtPurchase, Order order) {
        this.productCode = productCode;
        this.priceAtPurchase = priceAtPurchase;
        this.order = order;
    }

    public static OrderItem create(String productCode, Integer priceAtPurchase, Order order) {
        return new OrderItem(productCode, priceAtPurchase, order);
    }
}
