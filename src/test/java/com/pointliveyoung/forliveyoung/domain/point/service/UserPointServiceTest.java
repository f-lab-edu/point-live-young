package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.dto.response.UserPointResponse;
import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.entity.Status;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.PointPolicyRepository;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {
    @Mock
    private UserPointRepository repository;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPointService service;

    @Test
    @DisplayName("urgentAttendancePoint() - 정책이 비활성화면 적립하지 않는다")
    void urgentAttendancePoint_skip1() {
        User mockUser = mock(User.class);

        PointPolicy policy = mock(PointPolicy.class);
        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.ATTENDANCE))
                .thenReturn(Optional.of(policy));
        when(policy.getIsActivation()).thenReturn(false);

        service.urgentAttendancePoint(mockUser, PolicyType.ATTENDANCE);

        verify(repository, never()).save(any(UserPointLot.class));
    }

    @Test
    @DisplayName("urgentAttendancePoint() - 오늘 이미 로그인한 사용자는 적립하지 않는다")
    void urgentAttendancePoint_skip2() {
        User mockUser = mock(User.class);
        when(mockUser.getLastLoginAt()).thenReturn(LocalDateTime.now());

        PointPolicy policy = mock(PointPolicy.class);
        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.ATTENDANCE))
                .thenReturn(Optional.of(policy));
        when(policy.getIsActivation()).thenReturn(true);

        service.urgentAttendancePoint(mockUser, PolicyType.ATTENDANCE);

        verify(repository, never()).save(any(UserPointLot.class));
    }

    @Test
    @DisplayName("urgentAttendancePoint() 성공")
    void urgentAttendancePoint_success() {
        User user = mock(User.class);
        when(user.getLastLoginAt()).thenReturn(null);

        PointPolicy policy = mock(PointPolicy.class);
        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.ATTENDANCE))
                .thenReturn(Optional.of(policy));
        when(policy.getIsActivation()).thenReturn(true);
        when(policy.getPointAmount()).thenReturn(300);

        service.urgentAttendancePoint(user, PolicyType.ATTENDANCE);

        verify(repository).save(any(UserPointLot.class));
    }

    @Test
    @DisplayName("urgentAttendancePoint() 실패 - 존재하지 않은 포인트 정책")
    void urgentAttendancePoint_fail() {
        User user = mock(User.class);
        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.ATTENDANCE))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> service.urgentAttendancePoint(user, PolicyType.ATTENDANCE));

        assertEquals("포인트 정책이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("urgentSignUpPoint() - 정책 비활성화면 적립하지 않는다")
    void urgentSignUpPoint_skip() {
        User user = mock(User.class);

        PointPolicy policy = mock(PointPolicy.class);
        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.SIGN_UP))
                .thenReturn(Optional.of(policy));
        when(policy.getIsActivation()).thenReturn(false);

        service.urgentSignUpPoint(user, PolicyType.SIGN_UP);

        verify(repository, never()).save(any(UserPointLot.class));
    }

    @Test
    @DisplayName("urgentSignUpPoint() 정상")
    void urgentSignUpPoint_success() {
        User user = mock(User.class);

        PointPolicy policy = mock(PointPolicy.class);
        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.SIGN_UP))
                .thenReturn(Optional.of(policy));
        when(policy.getIsActivation()).thenReturn(true);
        when(policy.getPointAmount()).thenReturn(1000);

        service.urgentSignUpPoint(user, PolicyType.SIGN_UP);

        verify(repository, times(1)).save(any(UserPointLot.class));
    }

    @Test
    @DisplayName("urgentSignUpPoint() 실패 - 존재하지 않은 포인트 정책")
    void urgentSignUpPoint_fail() {
        User user = mock(User.class);
        when(pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.SIGN_UP))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> service.urgentSignUpPoint(user, PolicyType.SIGN_UP));

        assertEquals("포인트 정책이 존재하지 않습니다.", exception.getMessage());
    }


    @Test
    @DisplayName("getAllByUser() 성공")
    void getAllByUser_success() {
        Integer userId = 1;
        User user = mock(User.class);

        PointPolicy policy = PointPolicy.create(PolicyType.BIRTHDAY, 100, 4000);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        LocalDateTime localDateTime = LocalDateTime.now();

        UserPointLot entity1 = UserPointLot.create(user, policy, 1000);
        ReflectionTestUtils.setField(entity1, "createdAt", localDateTime);
        UserPointLot entity2 = UserPointLot.create(user, policy, 2000);
        ReflectionTestUtils.setField(entity2, "createdAt", localDateTime);

        List<UserPointLot> entityList = List.of(entity1, entity2);
        when(repository.findPointsByUser(any(Integer.class), any(Boolean.class), any(LocalDateTime.class))).thenReturn(entityList);

        UserPointResponse response1 = new UserPointResponse(1000, localDateTime, localDateTime.plusDays(100), Status.ACTIVE, PolicyType.BIRTHDAY);
        UserPointResponse response2 = new UserPointResponse(2000, localDateTime, localDateTime.plusDays(100), Status.ACTIVE, PolicyType.BIRTHDAY);

        List<UserPointResponse> expected = List.of(response1, response2);

        List<UserPointResponse> result = service.getAllByUser(userId);

        assertEquals(expected.size(), result.size());
        assertEquals(expected.get(0).pointBalance(), result.get(0).pointBalance());
        assertEquals(expected.get(1).pointBalance(), result.get(1).pointBalance());

        assertEquals(expected.get(0).createdAt(), result.get(0).createdAt());
        assertEquals(expected.get(1).createdAt(), result.get(1).createdAt());

        assertEquals(expected.get(0).status(), result.get(0).status());
        assertEquals(expected.get(1).status(), result.get(1).status());

        assertEquals(expected.get(0).policyType(), result.get(0).policyType());
        assertEquals(expected.get(1).policyType(), result.get(1).policyType());
    }

    @Test
    @DisplayName("getAllByUser 실패 - 존재하지 않은 사욪자")
    void getAllByUser_userNotFound() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> service.getAllByUser(any(Integer.class)));

        assertEquals("해당 사용자가 없습니다.", exception.getMessage());
    }

}