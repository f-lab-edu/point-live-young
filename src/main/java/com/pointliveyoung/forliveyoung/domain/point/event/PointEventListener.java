package com.pointliveyoung.forliveyoung.domain.point.event;

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
    public void onAttendancePointRegister(AttendancePointEvent event) {
        userPointService.urgentAttendancePoint(event.user());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSignUpPointRegister(SignUpPointEvent event) {
        userPointService.urgentSignUpPoint(event.user());
    }
}
