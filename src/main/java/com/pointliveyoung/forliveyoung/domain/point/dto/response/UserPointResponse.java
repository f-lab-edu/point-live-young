package com.pointliveyoung.forliveyoung.domain.point.dto.response;

import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.entity.Status;

import java.time.LocalDateTime;
import java.util.Objects;

public record UserPointResponse(
        Integer pointBalance,
        LocalDateTime createdAt,
        LocalDateTime expirationAt,
        Status status,
        PolicyType policyType) {

    public UserPointResponse {
        Objects.requireNonNull(pointBalance);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(expirationAt);
        Objects.requireNonNull(status);
        Objects.requireNonNull(policyType);
    }
}
