package com.pointliveyoung.forliveyoung.domain.point.dto.request;

import jakarta.validation.constraints.Min;

public record PointPolicyModifyRequest(

        @Min(value = 7, message = "만료일은 최소 7일 이상이어야 합니다.")
        Integer expirationDays,

        @Min(value = 1, message = "포인트 금액은 1 이상이어야 합니다.")
        Integer pointAmount) {
}
