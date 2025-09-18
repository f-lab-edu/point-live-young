package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.repository.PointPolicyRepository;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayPointService {
    private final UserService userService;
    private final PointPolicyRepository pointPolicyRepository;
    private final UserPointRepository userPointRepository;
    private final BirthdayPointRetryService birthdayPointRetryService;

    @Transactional
    public void grantBirthdayPoints() {
        LocalDate today = LocalDate.now();
        int monthValue = today.getMonthValue();
        int dayValue = today.getDayOfMonth();

        boolean leapYear = !today.isLeapYear() && monthValue == 2 && dayValue == 28;
        List<User> userList = userService.findByBirthDate(leapYear, monthValue, dayValue);

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);

        pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.BIRTHDAY)
                .filter(PointPolicy::getIsActivation)
                .ifPresent(policy ->
                        userList.stream()
                                .filter(user -> !userPointRepository.existsByUserAndPointPolicyAndCreatedAtBetween(user, policy, startOfDay, endOfDay))
                                .forEach(user -> birthdayPointRetryService.grantPointsToUser(user, policy)
                                )
                );
    }

}
