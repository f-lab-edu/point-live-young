package com.pointliveyoung.forliveyoung.domain.order.repository;

import com.pointliveyoung.forliveyoung.domain.order.entity.OrderPointUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderPointUsageRepository extends JpaRepository<OrderPointUsage, Integer> {
    List<OrderPointUsage> findByOrder_Id(Integer orderId);
}
