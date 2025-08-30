package com.pointliveyoung.forliveyoung.domain.user.repository;

import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
            select u from User u
            where month(u.birthDate) = :month
            and day(u.birthDate) = :day
            """)
    List<User> findByBirthDate(@Param("month") int month,
                               @Param("day") int day);
}
