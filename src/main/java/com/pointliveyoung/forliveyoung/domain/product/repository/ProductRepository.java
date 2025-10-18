package com.pointliveyoung.forliveyoung.domain.product.repository;


import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductQueryRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Integer id);

    @Query(value = """
            SELECT p.id,
                   p.product_name AS name,
                   p.stock,
                   p.price,
                   p.category
            FROM product p
            WHERE p.is_deleted = FALSE
              AND MATCH(p.product_name, p.description)
                  AGAINST (:kw IN NATURAL LANGUAGE MODE)
              AND (:cat IS NULL OR p.category = :cat)
              AND (:minP IS NULL OR p.price >= :minP)
              AND (:maxP IS NULL OR p.price <= :maxP)
              AND (:stockOnly = FALSE OR p.stock > 0)
            ORDER BY p.price ASC, p.product_name ASC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM product p
                    WHERE p.is_deleted = FALSE
                      AND MATCH(p.product_name, p.description)
                          AGAINST (:kw IN NATURAL LANGUAGE MODE)
                      AND (:cat IS NULL OR p.category = :cat)
                      AND (:minP IS NULL OR p.price >= :minP)
                      AND (:maxP IS NULL OR p.price <= :maxP)
                      AND (:stockOnly = FALSE OR p.stock > 0)
                    """,
            nativeQuery = true)
    Page<ProductBriefResponse> searchFulltext(
            @Param("kw") String keyword,
            @Param("cat") Category category,
            @Param("minP") Integer minPrice,
            @Param("maxP") Integer maxPrice,
            @Param("stockOnly") boolean stockOnly,
            Pageable pageable
    );
}
