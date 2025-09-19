package com.pointliveyoung.forliveyoung.domain.point.repository;

import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointRepository extends JpaRepository<UserPointLot, Integer>, UserPointRepositoryCustom {
}
