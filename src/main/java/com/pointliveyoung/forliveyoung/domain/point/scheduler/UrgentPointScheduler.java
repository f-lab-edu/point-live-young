package com.pointliveyoung.forliveyoung.domain.point.scheduler;

import com.pointliveyoung.forliveyoung.domain.point.service.BirthdayPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UrgentPointScheduler {
    private final BirthdayPointService birthdayPointService;

    @Scheduled(cron = "30 0 0 * * *")
    @Transactional
    public void urgentPointBirthday() {
        birthdayPointService.grantBirthdayPoints();
    }
}
