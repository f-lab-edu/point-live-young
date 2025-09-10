package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.dto.PointUsePlan;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class EarlyExpiryFirstPolicy implements PointUsePolicy {
    @Override
    public List<PointUsePlan> makePointUsePlan(List<UserPointLot> userPointLotList, int amount) {
        int remainPoint = amount;
        List<PointUsePlan> planList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        List<UserPointLot> lotSortedList = userPointLotList.stream()
                .filter(lot -> !lot.isExpired(now))
                .sorted(Comparator.comparing(UserPointLot::getExpirationAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        for (UserPointLot userPointLot : lotSortedList) {
            if (remainPoint <= 0) {
                break;
            }

            int usePoint = Math.min(userPointLot.getPointBalance(), remainPoint);
            if (usePoint > 0) {
                planList.add(new PointUsePlan(userPointLot.getId(), usePoint));

                remainPoint -= usePoint;
            }
        }


        if (remainPoint > 0) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        return planList;
    }
}
