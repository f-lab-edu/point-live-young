package com.pointliveyoung.forliveyoung.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, name = "user_name")
    private String name;

    @Column(nullable = false, unique = true, length = 100, name = "user_email")
    private String email;

    @Column(nullable = false, length = 100, name = "user_password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_role")
    private UserRole userRole;

    @Column(nullable = false, name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "last_login_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime lastLoginAt;

    private User(String name, String email, String password, LocalDate birthDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userRole = UserRole.USER;
        this.createdAt = LocalDateTime.now();
        this.birthDate = birthDate;
    }

    public static User of(String name, String email, String password, LocalDate birthDate) {
        return new User(name, email, password, birthDate);
    }

    public void changeRefreshToken(String refreshToken) {
        if (Objects.isNull(refreshToken) || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("리프레쉬 토큰은 null이거나 비어있을 수 없습니다.");
        }
        this.refreshToken = refreshToken;
    }

    public void invalidateRefreshToken() {
        this.refreshToken = null;
    }

    public void changeName(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("name 은 null이거나 비어있을 수 없습니다.");
        }
        this.name = name;
    }

    public void changePassword(String password) {
        if (Objects.isNull(password) || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 null이거나 비어있을 수 없습니다.");
        }
        this.password = password;
    }

    public void changeUserRole(UserRole userRole) {
        if (Objects.isNull(userRole)) {
            throw new IllegalArgumentException("UserRole 은 null이거나 비어있을 수 없습니다.");
        }
        this.userRole = userRole;
    }

    public void recordLogin(LocalDateTime localDateTime) {
        this.lastLoginAt = localDateTime;
    }
}
