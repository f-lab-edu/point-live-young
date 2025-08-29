package com.pointliveyoung.forliveyoung.domain.product.service;

import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductCreateRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductModifyRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import com.pointliveyoung.forliveyoung.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("create() 성공")
    void create_success() {
        ProductCreateRequest request = new ProductCreateRequest();
        ReflectionTestUtils.setField(request, "name", "productName");
        ReflectionTestUtils.setField(request, "description", "description");
        ReflectionTestUtils.setField(request, "stock", 10);
        ReflectionTestUtils.setField(request, "price", 1000);
        ReflectionTestUtils.setField(request, "category", Category.CAFE_SNACK);

        productService.create(request);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("modify() 성공")
    void modify_success() {
        Integer id = 1;
        Product entity = Product.create("oldName", "oldDesc", 10, 1000, Category.BEAUTY_HEALTH_CARE);
        when(productRepository.findById(id)).thenReturn(Optional.of(entity));

        ProductModifyRequest request = new ProductModifyRequest();
        ReflectionTestUtils.setField(request, "name", "newName");
        ReflectionTestUtils.setField(request, "description", "newDesc");
        ReflectionTestUtils.setField(request, "stock", 20);
        ReflectionTestUtils.setField(request, "price", 2000);
        ReflectionTestUtils.setField(request, "category", Category.CAFE_SNACK);
        System.out.println(request);

        productService.modify(id, request);

        assertEquals("newName", entity.getName());
        assertEquals("newDesc", entity.getDescription());
        assertEquals(20, entity.getStock());
        assertEquals(2000, entity.getPrice());
        assertEquals(Category.CAFE_SNACK, entity.getCategory());
    }

    @Test
    @DisplayName("modify() 실패 - 존재하지 않은 상품")
    void modify_fail_1() {
        when(productRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class, () -> {
            productService.modify(1, new ProductModifyRequest());
        });

        assertEquals("상품이 존재하지 않습니다.", noSuchElementException.getMessage());
    }

    @Test
    @DisplayName("delete() 성공")
    void delete_success() {
        Integer id = 1;
        Product entity = Product.create("oldName", "oldDesc", 10, 1000, Category.BEAUTY_HEALTH_CARE);
        when(productRepository.findById(id)).thenReturn(Optional.of(entity));

        productService.delete(id);

        assertTrue(entity.isDeleted());
    }

    @Test
    @DisplayName("delete() 실패 - 존재하지 않은 상품")
    void delete_fail_1() {
        when(productRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class, () -> {
            productService.delete(1);
        });

        assertEquals("상품이 존재하지 않습니다.", noSuchElementException.getMessage());
    }

    @Test
    @DisplayName("getProductById() 성공")
    void getProductById_success() {
        Integer id = 1;
        Product product = Product.create("이름", "설명", 5, 2000, Category.BEAUTY_HEALTH_CARE);
        ReflectionTestUtils.setField(product, "id", id);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductResponse expected = new ProductResponse(
                1,
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getPrice(),
                product.getCategory()
        );

        ProductResponse actual = productService.getProductById(id);

        assertNotNull(actual);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.stock(), actual.stock());
        assertEquals(expected.price(), actual.price());
        assertEquals(expected.category(), actual.category());
    }

    @Test
    @DisplayName("getProductById 실패 - 존재하지 않은 상품")
    void getProductById_fail_1() {
        when(productRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class, () -> {
            productService.getProductById(1);
        });

        assertEquals("상품이 존재하지 않습니다.", noSuchElementException.getMessage());
    }

    @Test
    @DisplayName("searchProducts() 성공")
    void searchProducts_success() {
        String keyword = "라떼";
        Category category = Category.CAFE_SNACK;
        Integer minPrice = 1000;
        Integer maxPrice = 10000;
        boolean inStock = true;
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "price"));

        List<ProductBriefResponse> content = List.of(
                new ProductBriefResponse(1, "돌체라떼", 30, 3000, Category.CAFE_SNACK),
                new ProductBriefResponse(2, "라떼", 15, 4500, Category.CAFE_SNACK)
        );
        Page<ProductBriefResponse> expected = new PageImpl<>(content, pageable, 2);

        when(productRepository.search(keyword, category, minPrice, maxPrice, inStock, pageable))
                .thenReturn(expected);


        Page<ProductBriefResponse> actual =
                productService.searchProducts(keyword, category, minPrice, maxPrice, inStock, pageable);

        assertNotNull(actual);
        assertEquals(actual.getContent().size(), 2);
        assertEquals(actual.getContent().get(0).id(), 1);
        assertEquals(actual.getContent().get(1).id(), 2);
        assertEquals(actual.getContent().get(0).name(), "돌체라떼");
        assertEquals(actual.getContent().get(1).name(), "라떼");
        assertEquals(actual.getContent().get(0).stock(), 30);
        assertEquals(actual.getContent().get(1).stock(), 15);
        assertEquals(actual.getContent().get(0).price(), 3000);
        assertEquals(actual.getContent().get(1).price(), 4500);
        assertEquals(actual.getContent().get(0).category(), Category.CAFE_SNACK);
        assertEquals(actual.getContent().get(1).category(), Category.CAFE_SNACK);
    }


}