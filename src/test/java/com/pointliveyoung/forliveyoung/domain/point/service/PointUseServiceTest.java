package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.order.entity.Order;
import com.pointliveyoung.forliveyoung.domain.order.repository.OrderPointUsageRepository;
import com.pointliveyoung.forliveyoung.domain.point.dto.PointUsePlan;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointUseServiceTest {
    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private OrderPointUsageRepository orderPointUsageRepository;

    @Mock
    private PointUsePolicy pointUsePolicy;

    @InjectMocks
    private PointUseService pointUseService;

    @Test
    @DisplayName("consume() 성공")
    void consume_success() {
        Integer userId = 1;
        int requireAmount = 100;
        Order order = mock(Order.class);

        UserPointLot lot1 = mock(UserPointLot.class);
        UserPointLot lot2 = mock(UserPointLot.class);
        when(lot1.getId()).thenReturn(1);
        when(lot2.getId()).thenReturn(2);

        when(userPointRepository.findPointsByUser(any(Integer.class), any(Boolean.class), any(LocalDateTime.class)))
                .thenReturn(List.of(lot1, lot2));

        List<PointUsePlan> plans = List.of(
                new PointUsePlan(1, 70),
                new PointUsePlan(2, 30)
        );
        when(pointUsePolicy.makePointUsePlan(anyList(), eq(requireAmount))).thenReturn(plans);

        when(lot1.dockBalance(eq(70), any(LocalDateTime.class))).thenReturn(70);
        when(lot2.dockBalance(eq(30), any(LocalDateTime.class))).thenReturn(30);

        pointUseService.consume(userId, requireAmount, order);

        verify(userPointRepository, times(1)).findPointsByUser(eq(userId), any(), any(LocalDateTime.class));
        verify(pointUsePolicy, times(1)).makePointUsePlan(anyList(), eq(requireAmount));

        verify(lot1).expireIfNeeded(any(LocalDateTime.class));
        verify(lot2).expireIfNeeded(any(LocalDateTime.class));
        verify(lot1).dockBalance(eq(70), any(LocalDateTime.class));
        verify(lot2).dockBalance(eq(30), any(LocalDateTime.class));

        verify(orderPointUsageRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    @DisplayName("consume() 실패 - requireAmount <= 0 이면 예외")
    void consume_fail() {
        Integer userId = 1;
        Order order = mock(Order.class);

        assertThrows(IllegalArgumentException.class, () -> pointUseService.consume(userId, 0, order));
    }
}