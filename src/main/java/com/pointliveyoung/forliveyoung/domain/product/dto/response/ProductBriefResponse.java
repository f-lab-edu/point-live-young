package com.pointliveyoung.forliveyoung.domain.product.dto.response;

import com.pointliveyoung.forliveyoung.domain.product.entity.Category;

import java.util.Objects;

public record ProductBriefResponse(Integer id, String name, Integer stock, Integer price, String category) {
    public ProductBriefResponse {
    }
}
