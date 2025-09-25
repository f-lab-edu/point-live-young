package com.pointliveyoung.forliveyoung.domain.point.dto;

import com.pointliveyoung.forliveyoung.domain.point.dto.request.PointPolicyCreateRequest;
import com.pointliveyoung.forliveyoung.domain.point.dto.response.PointPolicyResponse;
import com.pointliveyoung.forliveyoung.domain.point.dto.response.UserPointResponse;
import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;

import java.util.List;
import java.util.Objects;

public final class PointMapper {

    public static PointPolicy toEntity(PointPolicyCreateRequest request) {
        Objects.requireNonNull(request);
        return PointPolicy.create(
                request.policyType(),
                request.expirationDays(),
                request.pointAmount());
    }

    public static PointPolicyResponse toPointPolicyResponse(PointPolicy pointPolicy) {
        Objects.requireNonNull(pointPolicy);
        return new PointPolicyResponse(
                pointPolicy.getId(),
                pointPolicy.getPolicyType(),
                pointPolicy.getExpirationDays(),
                pointPolicy.getIsActive(),
                pointPolicy.getPointAmount());
    }

    public static List<PointPolicyResponse> toPointPolicyResponseList(List<PointPolicy> pointPolicies) {
        return pointPolicies.stream().map(PointMapper::toPointPolicyResponse).toList();
    }

    public static UserPointResponse toUserPointResponse(UserPointLot userPointLot) {
        return new UserPointResponse(
                userPointLot.getPointBalance(),
                userPointLot.getCreatedAt(),
                userPointLot.getExpirationAt(),
                userPointLot.getStatus(),
                userPointLot.getPointPolicy().getPolicyType()
        );
    }

    public static List<UserPointResponse> toUserPointResponseList(List<UserPointLot> userPointLots) {
        return userPointLots.stream().map(PointMapper::toUserPointResponse).toList();
    }

    private PointMapper() {
    }
}
