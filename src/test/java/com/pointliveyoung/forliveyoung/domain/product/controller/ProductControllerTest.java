package com.pointliveyoung.forliveyoung.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointliveyoung.forliveyoung.common.exception.GlobalExceptionHandler;
import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductCreateRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductModifyRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.pointliveyoung.forliveyoung.domain.product.service.ProductService;
import com.pointliveyoung.forliveyoung.domain.user.config.SecurityConfig;
import com.pointliveyoung.forliveyoung.domain.user.config.TestSecurityConfig;
import com.pointliveyoung.forliveyoung.domain.user.token.JwtAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
        }
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class ProductControllerTest {

    @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/products - 상품 추가 201 Created")
    void createProduct_success() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest();
        ReflectionTestUtils.setField(request, "name", "productName");
        ReflectionTestUtils.setField(request, "description", "description");
        ReflectionTestUtils.setField(request, "stock", 10);
        ReflectionTestUtils.setField(request, "price", 1000);
        ReflectionTestUtils.setField(request, "category", Category.CAFE_SNACK);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(productService, times(1)).create(any(ProductCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER 권한으로 상품 생성 시 403 Forbidden")
    void createProduct_fail() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest();
        ReflectionTestUtils.setField(request, "name", "productName");
        ReflectionTestUtils.setField(request, "description", "description");
        ReflectionTestUtils.setField(request, "stock", 10);
        ReflectionTestUtils.setField(request, "price", 1000);
        ReflectionTestUtils.setField(request, "category", Category.CAFE_SNACK);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("권한이 없습니다."));

        verify(productService, never()).create(any(ProductCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/products - 상품 수정 201 Created")
    void modifyProduct_success() throws Exception {
        ProductModifyRequest request = new ProductModifyRequest();
        ReflectionTestUtils.setField(request, "name", "productName");
        ReflectionTestUtils.setField(request, "description", "description");
        ReflectionTestUtils.setField(request, "stock", 10);
        ReflectionTestUtils.setField(request, "price", 1000);
        ReflectionTestUtils.setField(request, "category", Category.CAFE_SNACK);

        mockMvc.perform(put("/api/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(productService, times(1)).modify(any(Integer.class), any(ProductModifyRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /api/products/{id} - USER 권한으로 상품 수정 시 403 Forbidden")
    void modifyProduct_fail() throws Exception {
        ProductModifyRequest request = new ProductModifyRequest();
        ReflectionTestUtils.setField(request, "name", "productName");
        ReflectionTestUtils.setField(request, "description", "description");
        ReflectionTestUtils.setField(request, "stock", 10);
        ReflectionTestUtils.setField(request, "price", 1000);
        ReflectionTestUtils.setField(request, "category", Category.CAFE_SNACK);

        mockMvc.perform(put("/api/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("권한이 없습니다."));

        verify(productService, never()).modify(any(Integer.class), any(ProductModifyRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/users - 상품 삭제 204 NoContent")
    void deleteProduct_success() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(any(Integer.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/products/{id} - USER 권한으로 상품 삭제 시 403 Forbidden")
    void deleteProduct_fail() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("권한이 없습니다."));

        verify(productService, never()).delete(any(Integer.class));
    }

    @Test
    @DisplayName("GET /api/products/{id} - 상품 단일 조회 200 OK")
    void getProduct_success() throws Exception {
        ProductResponse response = new ProductResponse(1, "name", "des", 1, 1000, Category.CAFE_SNACK);

        when(productService.getProductById(10)).thenReturn(response);

        mockMvc.perform(get("/api/products/{id}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.description").value(response.description()))
                .andExpect(jsonPath("$.stock").value(response.stock()))
                .andExpect(jsonPath("$.price").value(response.price()))
                .andExpect(jsonPath("$.category").value(response.category().name()));
    }

    @Test
    @DisplayName("GET /api/products/search - 상품 검색 조회 페이지 응답 200 OK")
    void search_success() throws Exception {
        var pageReq = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "price"));
        var content = List.of(
                new ProductBriefResponse(1, "돌체라떼", 30, 3000, Category.CAFE_SNACK),
                new ProductBriefResponse(2, "라떼", 10, 4500, Category.CAFE_SNACK)
        );
        var page = new PageImpl<>(content, pageReq, 2);

        when(productService.searchProducts(any(), any(), any(), any(), anyBoolean(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "라떼")
                        .param("category", "CAFE_SNACK")
                        .param("minPrice", "1000")
                        .param("maxPrice", "5000")
                        .param("isStockAvailable", "true")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("돌체라떼"))
                .andExpect(jsonPath("$.content[0].stock").value(30))
                .andExpect(jsonPath("$.content[0].price").value(3000))
                .andExpect(jsonPath("$.content[0].category").value(Category.CAFE_SNACK.name()))

                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("라떼"))
                .andExpect(jsonPath("$.content[1].stock").value(10))
                .andExpect(jsonPath("$.content[1].price").value(4500))
                .andExpect(jsonPath("$.content[1].category").value(Category.CAFE_SNACK.name()));
    }

    @Test
    @DisplayName("GET /api/products/search - minPrice < 0 이면 400")
    void search_fail_1() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .param("minPrice", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("최소 가격은 0원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("GET /api/products/search - maxPrice < 0 이면 400")
    void search_fail_2() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .param("maxPrice", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("최대 가격은 0원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("GET /api/products/search - minPrice > maxPrice 이면 400")
    void search_fail_3() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .param("minPrice", "5000")
                        .param("maxPrice", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("최소 가격은 최대 가격보다 클 수 없습니다."));
    }


}