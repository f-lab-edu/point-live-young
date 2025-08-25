package com.pointliveyoung.forliveyoung.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false, length = 50, name = "user_name")
    private String name;

    @Column(nullable = false, unique = true, length = 100, name = "user_email")
    private String email;

    @Setter
    @Column(nullable = false, length = 100, name = "user_password")
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_role")
    private UserRole userRole;

    @Column(nullable = false, name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Setter
    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

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
}
