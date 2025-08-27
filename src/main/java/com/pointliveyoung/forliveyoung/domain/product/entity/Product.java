package com.pointliveyoung.forliveyoung.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import jakarta.validation.constraints.Min;

import java.util.Objects;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, length = 100, name = "product_name")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(0)
    @Column(nullable = false)
    @Check(constraints = "stock >= 0")
    private Integer stock;

    @Min(1)
    @Column(nullable = false)
    @Check(constraints = "price > 0")
    private Integer price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private boolean isDeleted;

    private Product(String name, String description, Integer stock, Integer price, Category category) {
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.category = category;
        this.isDeleted = false;
    }

    public static Product create(String name, String description, Integer stock, Integer price, Category category) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("상품명은 비어있을 수 없습니다.");
        }
        if (Objects.isNull(stock) || stock < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }
        if (Objects.isNull(price) || price <= 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }
        if (Objects.isNull(category)) {
            throw new IllegalArgumentException("카테고리는 비어있을 수 없습니다.");
        }
        return new Product(name, description, stock, price, category);
    }

    public void changeName(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("상품명은 비어있을 수 없습니다.");
        }
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStock(Integer stock) {
        if (Objects.isNull(stock) || stock < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }
        this.stock = stock;
    }

    public void changePrice(Integer price) {
        if (Objects.isNull(price) || price <= 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }
        this.price = price;
    }

    public void changeCategory(Category category) {
        if (Objects.isNull(category)) {
            throw new IllegalArgumentException("카테고리는 비어있을 수 없습니다.");
        }
        this.category = category;
    }

    public void delete() {
        this.isDeleted = true;
    }

}
