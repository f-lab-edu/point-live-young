package com.pointliveyoung.forliveyoung.domain.product.dto;

import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductCreateRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;

import java.util.Objects;

public final class ProductMapper {

    public static Product toEntity(ProductCreateRequest request) {
        Objects.requireNonNull(request);

        return Product.create(
                request.getName(),
                request.getDescription(),
                request.getStock(),
                request.getPrice(),
                request.getCategory()
        );
    }

    public static ProductResponse toProductResponse(Product product) {
        Objects.requireNonNull(product);
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getPrice(),
                product.getCategory()
        );
    }

    private ProductMapper() {
    }
}
