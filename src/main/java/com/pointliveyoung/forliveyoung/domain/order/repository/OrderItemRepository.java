package com.pointliveyoung.forliveyoung.domain.order.repository;

import com.pointliveyoung.forliveyoung.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder_Id(Integer orderId);
}
