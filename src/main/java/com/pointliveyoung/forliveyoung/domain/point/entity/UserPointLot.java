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

        if (!policy.isPermanent()) {
            this.expirationAt = this.createdAt.plusDays(policy.getExpirationDays());
        }

    }

    public static UserPointLot create(User user, PointPolicy policy, Integer pointBalance) {
        Objects.requireNonNull(user, "user는 null일 수 없습니다.");
        Objects.requireNonNull(policy, "pointPolicy는 null일 수 없습니다.");
        if (Objects.isNull(pointBalance) || pointBalance < 1) {
            throw new IllegalArgumentException("적립 포인트는 1 이상이어야 합니다.");
        }

        return new UserPointLot(user, policy, pointBalance);
    }

    public void expire(LocalDateTime now) {
        if (expirationAt != null && now.isAfter(expirationAt) && status == Status.ACTIVE) {
            this.status = Status.EXPIRED;
        }
    }

    public int dockBalance(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감 포인트는 1 이상이어야 한다");
        }

        if (this.status != Status.ACTIVE) {
            return 0;
        }

        if (Objects.nonNull(this.expirationAt) && LocalDateTime.now().isAfter(this.expirationAt)) {
            this.status = Status.EXPIRED;
            return 0;
        }
        int use = Math.min(this.pointBalance, amount);
        this.pointBalance -= use;

        if (this.pointBalance == 0) {
            this.status = Status.USED;
        }
        return use;
    }

    public void cancelPoint(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("다시 되돌아오는 포인트는 1 이상이어야 한다.");
        }
        this.pointBalance += amount;
        this.status = Status.ACTIVE;
    }
}
