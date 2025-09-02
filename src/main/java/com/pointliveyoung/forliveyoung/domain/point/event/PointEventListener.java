package com.pointliveyoung.forliveyoung.domain.point.event;

import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final UserPointService userPointService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAttendancePointRegister(PointEvent event) {
        userPointService.urgentAttendancePoint(event.user(), PolicyType.ATTENDANCE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSignUpPointRegister(PointEvent event) {
        userPointService.urgentSignUpPoint(event.user(), PolicyType.SIGN_UP);
    }
}
