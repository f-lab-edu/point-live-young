package com.pointliveyoung.forliveyoung.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendPurchaseSuccessMail(String toEmail, int quantity, int totalPrice, String productName, List<String> codeList) {

        String subject = "[ForLiveYoung] 구매가 완료되었습니다.";
        String body = """
                주문이 성공적으로 처리되었습니다.
                상품개수: %d개,
                결제 금액: %d원,
                상품 명: %s,
                상품 코드: %s
                                
                감사합니다.
                """
                .formatted(quantity, totalPrice, productName, String.join(", ", codeList));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    public void sendPurchaseCancelMail(String toEmail, int quantity, int totalCancelPrice, String productName) {
        String subject = "[ForLiveYoung] 구매 취소가 완료되었습니다.";
        String body = """
                주문이 성공적으로 취소되었습니다.
                상품개수: %d개,
                환불 금액: %d원,
                상품 명: %s
                                
                감사합니다.
                """
                .formatted(quantity, totalCancelPrice, productName);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
