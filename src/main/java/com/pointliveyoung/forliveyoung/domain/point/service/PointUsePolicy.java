package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.dto.PointUsePlan;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;

import java.util.List;

public interface PointUsePolicy {
    List<PointUsePlan> makePointUsePlan(List<UserPointLot> candidateLots, int amount);
}
