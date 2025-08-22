package com.pointliveyoung.forliveyoung.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Setter
    @Column(nullable = false, length = 100, name = "product_name")
    private String name;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(0)
    @Column(nullable = false)
    @Check(constraints = "stock >= 0")
    private Integer stock;

    @Min(1)
    @Setter
    @Column(nullable = false)
    @Check(constraints = "price > 0")
    private Integer price;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    public Product(String name, String description, Integer stock, Integer price, Category category) {
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.category = category;
    }

}
