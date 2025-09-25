package com.pointliveyoung.forliveyoung.domain.point.dto.response;

import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;

import java.util.Objects;

public record PointPolicyResponse(Integer id,
                                  PolicyType policyType,
                                  Integer expirationDays,
                                  Boolean isActive,
                                  Integer pointAmount) {

    public PointPolicyResponse {
        Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        Objects.requireNonNull(policyType, "type은 null일 수 없습니다.");
        Objects.requireNonNull(expirationDays, "expirationDays는 null일 수 없습니다.");
        Objects.requireNonNull(isActive, "isActive는 null일 수 없습니다.");
        Objects.requireNonNull(pointAmount, "pointAmount 는 null일 수 없습니다.");
    }
}
