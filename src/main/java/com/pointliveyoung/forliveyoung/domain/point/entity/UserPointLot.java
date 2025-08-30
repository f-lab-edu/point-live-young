package com.pointliveyoung.forliveyoung.domain.point.entity;

import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_point_lot")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserPointLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, name = "point_balance")
    private Integer pointBalance;

    @Column(nullable = false, name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(name = "expiration_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime expirationAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_policy_id", nullable = false)
    private PointPolicy pointPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private UserPointLot(User user, PointPolicy policy, Integer pointBalance) {
        this.pointBalance = pointBalance;
        this.user = user;
        this.pointPolicy = policy;
        this.createdAt = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    public static UserPointLot create(User user, PointPolicy policy, Integer pointBalance) {
        Objects.requireNonNull(user, "user는 null일 수 없습니다.");
        Objects.requireNonNull(policy, "pointPolicy는 null일 수 없습니다.");
        if (Objects.isNull(pointBalance) || pointBalance < 1) {
            throw new IllegalArgumentException("적립 포인트는 1 이상이어야 합니다.");
        }

        UserPointLot userPointLot = new UserPointLot(user, policy, pointBalance);
        if (!policy.isPermanent()) {
            userPointLot.expirationAt = userPointLot.getCreatedAt().plusDays(policy.getExpirationDays());
        }

        return userPointLot;
    }

    public void expire(LocalDateTime now) {
        if (expirationAt != null && now.isAfter(expirationAt) && status == Status.ACTIVE) {
            this.status = Status.EXPIRED;
        }
    }
}
