package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BirthdayPointRetryService {
    private final UserPointRepository userPointRepository;

    @Retryable(
            retryFor = {
                    PessimisticLockingFailureException.class,
                    QueryTimeoutException.class,
                    TransientDataAccessException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 400, multiplier = 2, maxDelay = 10_000, random = true)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void grantPointsToUser(User user, PointPolicy policy) {
        userPointRepository.save(UserPointLot.create(user, policy, policy.getPointAmount()));

    }

    @Recover
    public void recover(Exception e, User user, PointPolicy policy) {
        log.error("Birthday grant failed. userId={}, policyId={}", user.getId(), policy.getId(), e);
    }
}
