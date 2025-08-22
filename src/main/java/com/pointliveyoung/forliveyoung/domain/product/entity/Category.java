package com.pointliveyoung.forliveyoung.domain.product.entity;

import lombok.Getter;

@Getter
public enum Category {
    DIGITAL_GIFT("디지털 상품권"),
    CAFE_SNACK("카페/간식"),
    FOOD_CONVENIENCE("외식/편의점"),
    BEAUTY_HEALTH_CARE("뷰티/헬스/케어"),
    LIVING_SHOPPING("생활/쇼핑");

    private final String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }

}