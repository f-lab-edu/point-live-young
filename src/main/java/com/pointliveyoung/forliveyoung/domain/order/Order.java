package com.pointliveyoung.forliveyoung.domain.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "`order`")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order {
    @Id
    @Column(nullable = false, length = 200)
    private String code;

    @Column(nullable = false, name = "purchase_price")
    private Integer purchasePrice;

    @JsonFormat
    @Column(nullable = false, name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime updatedAt;

    @Column(nullable = false, name = "expired_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
