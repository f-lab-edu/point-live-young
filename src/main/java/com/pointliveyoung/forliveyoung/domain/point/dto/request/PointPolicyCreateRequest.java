package com.pointliveyoung.forliveyoung.domain.point.dto.request;

import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import jakarta.validation.constraints.*;

public record PointPolicyCreateRequest(
        @NotNull(message = "정책 타입은 필수입니다.")
        PolicyType policyType,

        @Min(value = 7, message = "만료일은 최소 7일 이상이어야 합니다.")
        Integer expirationDays,

        @NotNull(message = "포인트 금액은 필수입니다.")
        @Min(value = 1, message = "포인트 금액은 1 이상이어야 합니다.")
        Integer pointAmount) {
}
