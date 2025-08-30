package com.pointliveyoung.forliveyoung.domain.point.event;

import com.pointliveyoung.forliveyoung.domain.user.entity.User;

import java.util.Objects;

public record SignUpPointEvent(User user) {
    public SignUpPointEvent {
        Objects.requireNonNull(user, "user 는 null 일수는 없습니다.");
    }
}
