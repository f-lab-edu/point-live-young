package com.pointliveyoung.forliveyoung.domain.product.repository;

import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {
    Page<ProductBriefResponse> search(Category category,
                                      Integer minPrice,
                                      Integer maxPrice,
                                      boolean isStockAvailable,
                                      Pageable pageable
    );
}
