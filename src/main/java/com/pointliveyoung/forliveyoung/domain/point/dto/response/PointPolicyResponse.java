package com.pointliveyoung.forliveyoung.domain.point.dto.response;

import java.util.Objects;

public record PointPolicyResponse(Integer id,
                                  String name,
                                  Integer expirationDays,
                                  Boolean isActivation,
                                  Integer pointAmount) {

    public PointPolicyResponse {
        Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        Objects.requireNonNull(name, "name은 null일 수 없습니다.");
        Objects.requireNonNull(expirationDays, "expirationDays는 null일 수 없습니다.");
        Objects.requireNonNull(isActivation, "isActivation은 null일 수 없습니다.");
        Objects.requireNonNull(pointAmount, "pointAmount 는 null일 수 없습니다.");
    }
}
