package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(BirthdayPointRetryService.class)
class BirthdayPointRetryServiceTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.3")
            .withDatabaseName("forliveyoung_test")
            .withUsername("root")
            .withPassword("1234");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "false");
        registry.add("jwt.secret", () -> "test-secret");
        registry.add("jwt.access-token-seconds", () -> 900);
        registry.add("jwt.refresh-token-seconds", () -> 604800);
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Configuration
    @EnableRetry
    static class RetryTestConfig {
    }

    @MockitoBean
    private UserPointRepository userPointRepository;

    @Autowired
    private BirthdayPointRetryService birthdayPointRetryService;

    @DisplayName("일시적 예외가 여러 번 발생하다가 성공하면 총 4회 호출된다")
    @Test
    void grantPointsToUserTest() {
        User user = User.of("name", "@naver.com", "password", LocalDate.of(1999, 8, 8));
        PointPolicy pointPolicy = PointPolicy.create(PolicyType.BIRTHDAY, 100, 1000);

        ReflectionTestUtils.setField(user, "id", 1);
        ReflectionTestUtils.setField(pointPolicy, "id", 10);

        when(userPointRepository.save(any(UserPointLot.class)))
                .thenThrow(new QueryTimeoutException("timeout-1"))
                .thenThrow(new PessimisticLockingFailureException("lock-2"))
                .thenThrow(new TransientDataAccessResourceException("transient-3"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        birthdayPointRetryService.grantPointsToUser(user, pointPolicy);

        Mockito.verify(userPointRepository, times(4)).save(any(UserPointLot.class));
    }

    @DisplayName("재시도 최대 횟수(5회)까지 실패하면 @Recover가 처리된다")
    @Test
    void recoverTest() {
        User user = User.of("name", "@naver.com", "password", LocalDate.of(1999, 8, 8));
        PointPolicy pointPolicy = PointPolicy.create(PolicyType.BIRTHDAY, 100, 1000);

        when(userPointRepository.save(any(UserPointLot.class)))
                .thenThrow(new QueryTimeoutException("always-timeout"));


        birthdayPointRetryService.grantPointsToUser(user, pointPolicy);

        Mockito.verify(userPointRepository, times(5)).save(any(UserPointLot.class));
    }

}