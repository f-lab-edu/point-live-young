package com.pointliveyoung.forliveyoung.domain.product.repository;


import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
            SELECT p.id, p.name, p.stock, p.price, p.category from Product p
            where p.isDeleted = false
            and (:keyword is null
                     or lower(p.name) like lower(concat('%', :keyword, '%'))\s
                     or lower(p.description) like lower(concat('%', :keyword, '%'))
                     )
            and (:category is null or p.category = :category)
            and (:minPrice is null or p.price >= :minPrice)
            and (:maxPrice is null or p.price <= :maxPrice)
            and (:isStockAvailable = false or p.stock > 0)
            """)
    Page<ProductBriefResponse> search(@Param("keyword") String keyword,
                                      @Param("category") Category category,
                                      @Param("minPrice") Integer minPrice,
                                      @Param("maxPrice") Integer maxPrice,
                                      @Param("isStockAvailable") boolean isStockAvailable,
                                      Pageable pageable);
}
