package com.pointliveyoung.forliveyoung.domain.product.repository;

import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.pointliveyoung.forliveyoung.domain.product.entity.QProduct;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory query;

    private static final QProduct product = QProduct.product;

    @Override
    public Page<ProductBriefResponse> search(String keyword,
                                             Category category,
                                             Integer minPrice,
                                             Integer maxPrice,
                                             boolean isStockAvailable,
                                             Pageable pageable) {

        BooleanExpression isDeletedBooleanExpression = product.isDeleted.isFalse();
        BooleanExpression keyWordBooleanExpression = keyword(keyword);
        BooleanExpression categoryBooleanExpression = Objects.isNull(category) ? null : product.category.eq(category);
        BooleanExpression minPriceBooleanExpression = Objects.isNull(minPrice) ? null : product.price.goe(minPrice);
        BooleanExpression maxPriceBooleanExpression = Objects.isNull(maxPrice) ? null : product.price.loe(maxPrice);
        BooleanExpression stockBooleanExpression = isStockAvailable ? product.stock.gt(0) : null;


        List<ProductBriefResponse> queryResult = query.select(Projections.constructor(
                        ProductBriefResponse.class,
                        product.id,
                        product.name,
                        product.stock,
                        product.price,
                        product.category
                ))
                .from(product)
                .where(isDeletedBooleanExpression,
                        keyWordBooleanExpression,
                        categoryBooleanExpression,
                        minPriceBooleanExpression,
                        maxPriceBooleanExpression,
                        stockBooleanExpression)
                .orderBy(product.price.asc(), product.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = query.select(product.count())
                .from(product)
                .where(isDeletedBooleanExpression,
                        keyWordBooleanExpression,
                        categoryBooleanExpression,
                        minPriceBooleanExpression,
                        maxPriceBooleanExpression,
                        stockBooleanExpression)
                .fetchOne();


        return new PageImpl<>(queryResult, pageable, Objects.isNull(totalCount) ? 0 : totalCount);
    }

    private BooleanExpression keyword(String keyword) {
        if (Objects.isNull(keyword) || keyword.trim().isEmpty()) return null;
        return product.name.containsIgnoreCase(keyword)
                .or(product.description.containsIgnoreCase(keyword));
    }
}
