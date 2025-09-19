package com.pointliveyoung.forliveyoung.domain.point.repository;

import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPointRepositoryCustom {
    void updateStatusToExpiredByUser(Integer userId, LocalDateTime now);

    List<UserPointLot> findPointsByUser(Integer userId, boolean activeOnly, LocalDateTime now);
}
