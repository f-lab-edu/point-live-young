package com.pointliveyoung.forliveyoung.domain.product.repository;


import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductQueryRepository {

}
