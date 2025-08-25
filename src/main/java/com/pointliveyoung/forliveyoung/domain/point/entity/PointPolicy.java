package com.pointliveyoung.forliveyoung.domain.point.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
