package com.pointliveyoung.forliveyoung.domain.point.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record PointPolicyModifyRequest(
        @Size(max = 100, message = "정책명은 최대 100자까지 가능합니다.")
        String name,

        @Min(value = 7, message = "만료일은 최소 7일 이상이어야 합니다.")
        Integer expirationDays,

        @Min(value = 1, message = "포인트 금액은 1 이상이어야 합니다.")
        Integer pointAmount) {
}
