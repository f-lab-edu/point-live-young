package com.pointliveyoung.forliveyoung.domain.order.event;

import com.pointliveyoung.forliveyoung.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PurchaseEmailHandler {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseCompleted(PurchaseCompletedEvent event){
        emailService.sendPurchaseSuccessMail(
                event.userEmail(),
                event.quantity(),
                event.totalPrice(),
                event.productName(),
                event.codeList()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseCancel(PurchaseCancelEvent event){
        emailService.sendPurchaseCancelMail(
                event.userEmail(),
                event.quantity(),
                event.totalCancelPrice(),
                event.productName()
        );
    }
}
