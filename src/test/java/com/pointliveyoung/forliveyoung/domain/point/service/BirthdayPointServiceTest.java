package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.repository.PointPolicyRepository;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BirthdayPointServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private BirthdayPointRetryService birthdayPointRetryService;

    @InjectMocks
    private BirthdayPointService birthdayPointService;

    @DisplayName("이미 지급된 유저는 제외하고 미지급 유저만 포인트 지급된다")
    @Test
    void grantBirthdayPointsTest1() {
        User user1 = User.of("name", "@naver.com", "password", LocalDate.of(1999, 8, 8));
        User user2 = User.of("name2", "2@naver.com", "password2", LocalDate.of(1999, 8, 8));

        PointPolicy pointPolicy = PointPolicy.create(PolicyType.BIRTHDAY, 100, 1000);

        when(userService.findByBirthDate(any(Boolean.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(user1, user2));

        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.BIRTHDAY))
                .thenReturn(Optional.of(pointPolicy));

        when(userPointRepository.existsByUserAndPointPolicyAndCreatedAtBetween(
                eq(user1), eq(pointPolicy), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);
        when(userPointRepository.existsByUserAndPointPolicyAndCreatedAtBetween(
                eq(user2), eq(pointPolicy), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        birthdayPointService.grantBirthdayPoints();

        verify(birthdayPointRetryService, times(1)).grantPointsToUser(eq(user2), eq(pointPolicy));
        verify(birthdayPointRetryService, never()).grantPointsToUser(eq(user1), eq(pointPolicy));
    }

    @DisplayName("정책이 비활성화 상태이면 포인트 지급이 실행되지 않는다")
    @Test
    void grantBirthdayPointsTest2() {
        User user1 = User.of("name", "@naver.com", "password", LocalDate.of(1999, 8, 8));
        User user2 = User.of("name2", "2@naver.com", "password2", LocalDate.of(1999, 8, 8));

        PointPolicy pointPolicy = PointPolicy.create(PolicyType.BIRTHDAY, 100, 1000);

        when(userService.findByBirthDate(any(Boolean.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(user1, user2));

        ReflectionTestUtils.setField(pointPolicy, "isActivation", false);

        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.BIRTHDAY))
                .thenReturn(Optional.of(pointPolicy));

        birthdayPointService.grantBirthdayPoints();

        verify(userPointRepository, never()).existsByUserAndPointPolicyAndCreatedAtBetween(any(), any(), any(), any());
        verify(birthdayPointRetryService, never()).grantPointsToUser(any(), any());
    }

    @DisplayName("생일 포인트 정책이 존재하지 않으면 포인트 지급이 실행되지 않는다")
    @Test
    void grantBirthdayPointsTest3() {
        User user1 = User.of("name", "@naver.com", "password", LocalDate.of(1999, 8, 8));
        User user2 = User.of("name2", "2@naver.com", "password2", LocalDate.of(1999, 8, 8));

        when(userService.findByBirthDate(any(Boolean.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(user1, user2));

        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.BIRTHDAY))
                .thenReturn(Optional.empty());

        birthdayPointService.grantBirthdayPoints();

        verify(userPointRepository, never()).existsByUserAndPointPolicyAndCreatedAtBetween(any(), any(), any(), any());
        verify(birthdayPointRetryService, never()).grantPointsToUser(any(), any());
    }
}