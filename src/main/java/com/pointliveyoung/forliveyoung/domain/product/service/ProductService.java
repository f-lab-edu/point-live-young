package com.pointliveyoung.forliveyoung.domain.product.service;

import com.pointliveyoung.forliveyoung.domain.product.dto.ProductMapper;
import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductCreateRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.request.ProductModifyRequest;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductBriefResponse;
import com.pointliveyoung.forliveyoung.domain.product.dto.response.ProductResponse;
import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import com.pointliveyoung.forliveyoung.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void create(ProductCreateRequest request) {

        Product product = ProductMapper.toEntity(request);

        productRepository.save(product);
    }

    @Transactional
    public void modify(Integer productId, ProductModifyRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

        Optional.ofNullable(request.getName()).ifPresent(product::changeName);
        Optional.ofNullable(request.getDescription()).ifPresent(product::changeDescription);
        Optional.ofNullable(request.getStock()).ifPresent(product::changeStock);
        Optional.ofNullable(request.getPrice()).ifPresent(product::changePrice);
        Optional.ofNullable(request.getCategory()).ifPresent(product::changeCategory);
    }

    @Transactional
    public void delete(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));
        product.delete();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

        return ProductMapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductBriefResponse> searchProducts(String keyword, Category category, Integer minPrice, Integer maxPrice, boolean isStockAvailable, Pageable pageable) {

        if (Objects.isNull(keyword) || keyword.isBlank()) {

        return productRepository.search( category, minPrice, maxPrice, isStockAvailable, pageable);
        }

        return productRepository.searchFulltext(keyword, category, minPrice, maxPrice, isStockAvailable, pageable);
    }

    @Transactional(readOnly = true)
    public Product getById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));
    }

    @Transactional
    public Product getByIdForUpdate(Integer productId) {
        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));
    }

    @Transactional
    public void decreaseStock(Product product, int stock) {
        product.decreaseStock(stock);
    }

    @Transactional
    public void increaseStock(Product product, int stock) {
        product.increaseStock(stock);
    }

}