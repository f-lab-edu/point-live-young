package com.pointliveyoung.forliveyoung.domain.point.dto;

import com.pointliveyoung.forliveyoung.domain.point.dto.request.PointPolicyCreateRequest;
import com.pointliveyoung.forliveyoung.domain.point.dto.response.PointPolicyResponse;
import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;

import java.util.List;
import java.util.Objects;

public final class PointPolicyMapper {

    public static PointPolicy toEntity(PointPolicyCreateRequest request) {
        Objects.requireNonNull(request);
        return PointPolicy.create(
                request.name(),
                request.expirationDays(),
                request.pointAmount());
    }

    public static PointPolicyResponse toPointPolicyResponse(PointPolicy pointPolicy) {
        Objects.requireNonNull(pointPolicy);
        return new PointPolicyResponse(
                pointPolicy.getId(),
                pointPolicy.getName(),
                pointPolicy.getExpirationDays(),
                pointPolicy.getIsActivation(),
                pointPolicy.getPointAmount());
    }

    public static List<PointPolicyResponse> toPointPolicyResponseList(List<PointPolicy> pointPolicies) {
        return pointPolicies.stream().map(PointPolicyMapper::toPointPolicyResponse).toList();
    }

    private PointPolicyMapper() {
    }
}
