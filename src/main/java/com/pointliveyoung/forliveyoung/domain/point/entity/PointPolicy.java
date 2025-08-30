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

    @Column(nullable = false, length = 100, name = "point_policy_name")
    private String name;

    @Column(nullable = false, name = "expiration_days")
    private Integer expirationDays;

    @Column(nullable = false, name = "is_activation")
    private Boolean isActivation;

    @Column(nullable = false, name = "point_amount")
    private Integer pointAmount;


    private PointPolicy(String name, Integer expirationDays, Integer pointAmount) {
        this.name = name;
        this.expirationDays = expirationDays;
        this.pointAmount = pointAmount;
        this.isActivation = true;
    }

    public static PointPolicy create(String name, Integer expirationDays, Integer pointAmount) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("포인트 정책 명은 비어있으면 안됩니다.");
        }

        if (Objects.isNull(expirationDays) || expirationDays < 7) {
            throw new IllegalArgumentException("만료 일수는 최소 7일 이상이어야 한다.");
        }

        if (Objects.isNull(pointAmount) || pointAmount < 1) {
            throw new IllegalArgumentException("포인트 금액은 1 이상이어야 한다.");
        }

        return new PointPolicy(name, expirationDays, pointAmount);
    }


    public void changeName(String name) {
        this.name = name;
    }

    public void changeExpirationDays(Integer days) {
        this.expirationDays = days;
    }

    public void changeIsActivation(Boolean active) {
        this.isActivation = active;
    }

    public void changePointAmount(Integer amount) {
        this.pointAmount = amount;
    }
}
