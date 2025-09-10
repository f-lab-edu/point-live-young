package com.pointliveyoung.forliveyoung.domain.product.repository;

import com.pointliveyoung.forliveyoung.common.config.QuerydslConfig;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfig.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductQueryRepositoryImplTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.3")
            .withDatabaseName("forliveyoung_test")
            .withUsername("root")
            .withPassword("1234");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "false");
    }

    @Autowired
    private ProductRepository productRepository;

    private Category category1;
    private Category category2;


    @BeforeEach
    void setUp() {
        Category[] categoryValues = Category.values();
        category1 = categoryValues[0];
        category2 = categoryValues[1];
        Product deleteProduct = Product.create("syntha 6", "protein supplements", 6, 3000, category2);
        deleteProduct.delete();

        List<Product> productList = Arrays.asList(
                Product.create("medi Serum", "vitamin serum", 10, 5000, category1),
                Product.create("dr.g cleanser", "mild cleanser", 0, 6000, category1),
                Product.create("torriden oil", "face oil", 50, 7000, category1),
                Product.create("estra Mask", "sheet mask", 20, 2000, category2),
                Product.create("torriden brush", "make up brush", 4, 16000, category2),
                deleteProduct);

        productRepository.saveAll(productList);
    }

    @Test
    @DisplayName("")
    void search_test() {
        PageRequest page = PageRequest.of(0, 10);
        Page<ProductBriefResponse> result = productRepository.search(null, null, null, null, false, page);

        assertEquals(5, result.getTotalElements());
    }

    @Test
    @DisplayName("keyword(name/description) 검색")
    void search_test_keyword() {
        PageRequest page = PageRequest.of(0, 10);
        Page<ProductBriefResponse> result = productRepository.search(
                "serum", null, null, null, false, page);

        assertEquals(1, result.getTotalElements());

    }

    @Test
    @DisplayName("카테고리 필터")
    void search_test_category() {
        PageRequest page = PageRequest.of(0, 10);
        Page<ProductBriefResponse> result = productRepository.search(
                null, category1, null, null, false, page);

        assertEquals(3, result.getTotalElements());
    }

    @Test
    @DisplayName("최대 가격 이하(maxPrice) 조건 필터")
    void search_test_price_max() {
        PageRequest page = PageRequest.of(0, 10);
        Page<ProductBriefResponse> result = productRepository.search(
                null, null, null, 7000, false, page);

        assertEquals(4, result.getTotalElements());
    }

    @Test
    @DisplayName("최소 가격 이상(minPrice) 조건 필터")
    void search_test_price_min() {
        PageRequest page = PageRequest.of(0, 10);
        Page<ProductBriefResponse> result = productRepository.search(
                null, null, 6000, null, false, page);

        assertEquals(3, result.getTotalElements());
    }

    @Test
    @DisplayName("최소, 최대 가격 범위(minPrice~maxPrice) 조건 필터")
    void search_test_price_min_max() {
        PageRequest page = PageRequest.of(0, 10);
        Page<ProductBriefResponse> result = productRepository.search(
                null, null, 7000, 20000, false, page);

        assertEquals(2, result.getTotalElements());
    }


    @Test
    @DisplayName("재고 있음(isStockAvailable=true)만")
    void search_test_isStock() {
        PageRequest page = PageRequest.of(0, 10);
        Page<ProductBriefResponse> result = productRepository.search(
                null, null, null, null, true, page);

        assertEquals(4, result.getTotalElements());
    }

    @Test
    @DisplayName("페이징: page=0 size=2 → 2건 / 총 5건")
    void search_test_page() {
        PageRequest page = PageRequest.of(0, 2);
        Page<ProductBriefResponse> result = productRepository.search(
                null, null, null, null, false, page);

        assertEquals(5, result.getTotalElements());
        assertEquals(2, result.getContent().size());


    }

}