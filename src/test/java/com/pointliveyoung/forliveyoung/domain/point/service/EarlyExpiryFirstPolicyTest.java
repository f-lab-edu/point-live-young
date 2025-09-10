package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.dto.PointUsePlan;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EarlyExpiryFirstPolicyTest {
    private final EarlyExpiryFirstPolicy policy = new EarlyExpiryFirstPolicy();

    @Test
    @DisplayName("만료 임박순으로 여러 lot에서 필요한 만큼 배분")
    void makePointUsePlan_success1() {
        LocalDateTime now = LocalDateTime.now();

        UserPointLot lot1 = mock(UserPointLot.class);
        when(lot1.getId()).thenReturn(1);
        when(lot1.isExpired(any(LocalDateTime.class))).thenReturn(false);
        when(lot1.getExpirationAt()).thenReturn(now.plusDays(1));
        when(lot1.getPointBalance()).thenReturn(30);

        UserPointLot lot2 = mock(UserPointLot.class);
        when(lot2.getId()).thenReturn(2);
        when(lot2.isExpired(any(LocalDateTime.class))).thenReturn(false);
        when(lot2.getExpirationAt()).thenReturn(now.plusDays(3));
        when(lot2.getPointBalance()).thenReturn(50);

        UserPointLot lot3 = mock(UserPointLot.class);
        when(lot3.getId()).thenReturn(3);
        when(lot3.isExpired(any(LocalDateTime.class))).thenReturn(false);
        when(lot3.getExpirationAt()).thenReturn(now.plusDays(7));
        when(lot3.getPointBalance()).thenReturn(100);

        int required = 120;

        List<PointUsePlan> plans = policy.makePointUsePlan(List.of(lot1, lot2, lot3), required);

        assertEquals(3, plans.size());
        assertEquals(1, plans.get(0).userPointLotId());
        assertEquals(30, plans.get(0).useAmount());
        assertEquals(2, plans.get(1).userPointLotId());
        assertEquals(50, plans.get(1).useAmount());
        assertEquals(3, plans.get(2).userPointLotId());
        assertEquals(40, plans.get(2).useAmount());
    }

    @Test
    @DisplayName("만료된 UserPointLot은 제외하고 계획을 세운다")
    void makePointUsePlan_success2() {
        LocalDateTime now = LocalDateTime.now();

        UserPointLot expired = mock(UserPointLot.class);
        when(expired.getId()).thenReturn(1);
        when(expired.isExpired(any(LocalDateTime.class))).thenReturn(true);

        UserPointLot active = mock(UserPointLot.class);
        when(active.getId()).thenReturn(10);
        when(active.isExpired(any(LocalDateTime.class))).thenReturn(false);
        when(active.getExpirationAt()).thenReturn(now.plusDays(2));
        when(active.getPointBalance()).thenReturn(80);

        List<PointUsePlan> plans = policy.makePointUsePlan(List.of(expired, active), 50);

        assertEquals(1, plans.size());
        assertEquals(10, plans.get(0).userPointLotId());
        assertEquals(50, plans.get(0).useAmount());
    }

    @Test
    @DisplayName("잔액 총합이 부족하면 예외")
    void makePointUsePlan_fail() {
        LocalDateTime now = LocalDateTime.now();

        UserPointLot lot = mock(UserPointLot.class);
        when(lot.isExpired(any(LocalDateTime.class))).thenReturn(false);
        when(lot.getExpirationAt()).thenReturn(now.plusDays(1));
        when(lot.getPointBalance()).thenReturn(40);

        assertThrows(IllegalStateException.class, () -> policy.makePointUsePlan(List.of(lot), 50));
    }

    @Test
    @DisplayName("expirationAt이 null(영구 포인트)인 lot는 정렬상 맨 뒤(nullsLast)")
    void makePointUsePlan_success3() {
        LocalDateTime now = LocalDateTime.now();

        UserPointLot expSoon = mock(UserPointLot.class);
        when(expSoon.getId()).thenReturn(1);
        when(expSoon.isExpired(now)).thenReturn(false);
        when(expSoon.getExpirationAt()).thenReturn(now.plusDays(1));
        when(expSoon.getPointBalance()).thenReturn(10);

        UserPointLot permanent = mock(UserPointLot.class);
        when(permanent.getId()).thenReturn(2);
        when(permanent.isExpired(now)).thenReturn(false);
        when(permanent.getExpirationAt()).thenReturn(null);
        when(permanent.getPointBalance()).thenReturn(100);

        List<PointUsePlan> plans = policy.makePointUsePlan(List.of(permanent, expSoon), 50);

        assertEquals(2, plans.size());
        assertEquals(1, plans.get(0).userPointLotId());
        assertEquals(10, plans.get(0).useAmount());

        assertEquals(2, plans.get(1).userPointLotId());
        assertEquals(40, plans.get(1).useAmount());
    }
}