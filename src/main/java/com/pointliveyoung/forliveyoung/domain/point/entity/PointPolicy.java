package com.pointliveyoung.forliveyoung.domain.point.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "point_policy")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PointPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "policy_type", unique = true, length = 50)
    private PolicyType policyType;

    @Column(nullable = false, name = "expiration_days")
    private Integer expirationDays;

    @Column(nullable = false, name = "is_activation")
    private Boolean isActivation;

    @Column(nullable = false, name = "point_amount")
    private Integer pointAmount;

    public static PointPolicy create(PolicyType policyType, Integer expirationDays, Integer pointAmount) {
        if (Objects.isNull(policyType)) {
            throw new IllegalArgumentException("PointPolicyType 은 null 일 수 없습니다.");
        }
        if (Objects.isNull(pointAmount) || pointAmount < 1) {
            throw new IllegalArgumentException("포인트 금액(pointAmount)은 1 이상이어야 합니다.");
        }

        if (Objects.nonNull(expirationDays) && expirationDays < 7) {
            throw new IllegalArgumentException("만료 일수 expirationDays는 최소 7일 이상이어야 합니다. (또는 null은 영구)");
        }
        return new PointPolicy(policyType, expirationDays, pointAmount);
    }

    private PointPolicy(PolicyType policyType, Integer expirationDays, Integer pointAmount) {
        this.policyType = policyType;
        this.expirationDays = expirationDays;
        this.pointAmount = pointAmount;
        this.isActivation = true;
    }

    public void changeExpirationDays(Integer days) {
        this.expirationDays = days;
    }

    public void changePointAmount(Integer amount) {
        this.pointAmount = amount;
    }

    public void toggleActivation() {
        this.isActivation = !this.isActivation;
    }

    public boolean isPermanent() {
        return Objects.isNull(this.expirationDays);
    }
}
