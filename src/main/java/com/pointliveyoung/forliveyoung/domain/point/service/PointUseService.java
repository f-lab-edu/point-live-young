package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.order.entity.Order;
import com.pointliveyoung.forliveyoung.domain.order.entity.OrderPointUsage;
import com.pointliveyoung.forliveyoung.domain.order.repository.OrderPointUsageRepository;
import com.pointliveyoung.forliveyoung.domain.point.dto.PointUsePlan;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointUseService {
    private final UserPointRepository userPointRepository;
    private final OrderPointUsageRepository orderPointUsageRepository;
    private final PointUsePolicy pointUsePolicy;

    @Transactional
    public void consume(Integer userId, int requireAmount, Order order) {
        if (requireAmount <= 0) {
            throw new IllegalArgumentException("차감 포인트는 1 이상이어야 한다.");
        }

        LocalDateTime now = LocalDateTime.now();

        List<UserPointLot> userPointLotList = userPointRepository.findPointsByUser(userId, true, now);

        List<PointUsePlan> planList = pointUsePolicy.makePointUsePlan(userPointLotList, requireAmount);

        Map<Integer, UserPointLot> map = userPointLotList.stream()
                .collect(Collectors.toMap(UserPointLot::getId, it -> it));

        List<OrderPointUsage> usages = new ArrayList<>();

        for (PointUsePlan plan : planList) {
            UserPointLot userPointLot = map.get(plan.userPointLotId());
            userPointLot.expireIfNeeded(now);

            int usedAmount = userPointLot.dockBalance(plan.useAmount(), now);
            if (usedAmount > 0) {
                usages.add(OrderPointUsage.create(order, userPointLot, usedAmount));
            }

        }

        orderPointUsageRepository.saveAll(usages);
    }
}
