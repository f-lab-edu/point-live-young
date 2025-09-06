package com.pointliveyoung.forliveyoung.domain.order.entity;

import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_point_usage")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderPointUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_point_lot_id", nullable = false)
    private UserPointLot lot;

    @Column(nullable = false, name = "used_amount")
    private Integer usedAmount;

    @Column(nullable = false, name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    private OrderPointUsage(Order order, UserPointLot lot, Integer usedAmount) {
        this.order = order;
        this.lot = lot;
        this.usedAmount = usedAmount;
        this.createdAt = LocalDateTime.now();
    }

    public static OrderPointUsage create(Order order, UserPointLot lot, Integer usedAmount) {
        return new OrderPointUsage(order, lot, usedAmount);
    }
}
