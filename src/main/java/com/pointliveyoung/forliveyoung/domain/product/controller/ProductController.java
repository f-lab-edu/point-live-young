package com.pointliveyoung.forliveyoung.domain.product.controller;

import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductCreateRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductModifyRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.pointliveyoung.forliveyoung.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        productService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> modifyProduct(@PathVariable Integer productId,
                                              @Valid @RequestBody ProductModifyRequest request) {
        productService.modify(productId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer productId) {
        productService.delete(productId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Integer productId) {
        ProductResponse productResponse = productService.getProductById(productId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductBriefResponse>> searchProduct(@RequestParam(required = false) String keyword,
                                                                    @RequestParam(required = false) Category category,
                                                                    @RequestParam(required = false) Integer minPrice,
                                                                    @RequestParam(required = false) Integer maxPrice,
                                                                    @RequestParam(required = false, defaultValue = "false") boolean isStockAvailable,
                                                                    @PageableDefault(size = 20, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {

        if (Objects.nonNull(minPrice) && minPrice < 0) {
            throw new IllegalArgumentException("최소 가격은 0원 이상이어야 합니다.");
        }

        if (Objects.nonNull(maxPrice) && maxPrice < 0) {
            throw new IllegalArgumentException("최대 가격은 0원 이상이어야 합니다.");
        }

        if (Objects.nonNull(minPrice) && Objects.nonNull(maxPrice) && minPrice > maxPrice) {
            throw new IllegalArgumentException("최소 가격은 최대 가격보다 클 수 없습니다.");
        }

        Page<ProductBriefResponse> response = productService.searchProducts(keyword, category, minPrice, maxPrice, isStockAvailable, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
