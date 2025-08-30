package com.pointliveyoung.forliveyoung.domain.point.scheduler;

import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.PointPolicyRepository;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrgentPointScheduler {
    private final UserRepository userRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final UserPointRepository userPointRepository;

    @Scheduled(cron = "30 0 0 * * *")
    @Transactional
    public void urgentPointBirthday() {
        LocalDate today = LocalDate.now();
        int monthValue = today.getMonthValue();
        int dayValue = today.getDayOfMonth();

        boolean leapYear = !today.isLeapYear() && monthValue == 2 && dayValue == 28;

        List<User> userList = leapYear
                ? userRepository.findByBirthDate(monthValue, dayValue - 1)
                : userRepository.findByBirthDate(monthValue, dayValue);


        pointPolicyRepository.findPointPolicyByPolicyType(PolicyType.BIRTHDAY).filter(PointPolicy::getIsActivation).ifPresent(policy -> {

            for (User user : userList) {
                userPointRepository.save(UserPointLot.create(user, policy, policy.getPointAmount()));
            }
        });
    }
}
