package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.dto.PointMapper;
import com.pointliveyoung.forliveyoung.domain.point.dto.response.UserPointResponse;
import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.PointPolicyRepository;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserPointRepository userPointRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final UserRepository userRepository;

    @Transactional
    public void urgentAttendancePoint(User user) {
        PointPolicy pointPolicy = pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.ATTENDANCE)
                .orElseThrow(() -> new NoSuchElementException("포인트 정책이 존재하지 않습니다."));

        if (!pointPolicy.getIsActivation()) {
            return;
        }

        if (Objects.nonNull(user.getLastLoginAt()) && user.getLastLoginAt().toLocalDate().isEqual(LocalDate.now())) {
            return;
        }

        UserPointLot userPointLot = UserPointLot.create(user, pointPolicy, pointPolicy.getPointAmount());
        userPointRepository.save(userPointLot);
    }

    @Transactional
    public void urgentSignUpPoint(User user) {
        PointPolicy pointPolicy = pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.SIGN_UP)
                .orElseThrow(() -> new NoSuchElementException("포인트 정책이 존재하지 않습니다."));

        if (!pointPolicy.getIsActivation()) {
            return;
        }
        UserPointLot userPointLot = UserPointLot.create(user, pointPolicy, pointPolicy.getPointAmount());
        userPointRepository.save(userPointLot);
    }

    @Transactional(readOnly = true)
    public List<UserPointResponse> getAllByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        List<UserPointLot> userPointLotList = userPointRepository.findAllByUserOrderByCreatedAtDesc(user);

        return PointMapper.toUserPointResponseList(userPointLotList);
    }
}
