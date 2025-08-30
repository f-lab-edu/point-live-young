package com.pointliveyoung.forliveyoung.domain.product.dto.response;

import com.pointliveyoung.forliveyoung.domain.product.entity.Category;

import java.util.Objects;

public record ProductResponse(Integer id, String name, String description, Integer stock, Integer price,
                              Category category) {
    public ProductResponse {
        Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        Objects.requireNonNull(name, "name은 null일 수 없습니다.");
        Objects.requireNonNull(stock, "stock은 null일 수 없습니다.");
        Objects.requireNonNull(price, "price는 null일 수 없습니다.");
        Objects.requireNonNull(category, "category는 null일 수 없습니다.");
    }
}
