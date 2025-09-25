package com.pointliveyoung.forliveyoung.domain.point.service;

import com.pointliveyoung.forliveyoung.domain.point.dto.request.PointPolicyModifyRequest;
import com.pointliveyoung.forliveyoung.domain.point.dto.response.PointPolicyResponse;
import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.repository.PointPolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointPolicyServiceTest {

    @Mock
    private PointPolicyRepository repository;

    @InjectMocks
    private PointPolicyService service;

    @Test
    @DisplayName("modify() 성공")
    void modify_success() {
        Integer id = 10;

        PointPolicy entity = PointPolicy.create(PolicyType.BIRTHDAY, 1000, 4000);

        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(entity));

        PointPolicyModifyRequest request = new PointPolicyModifyRequest(3000, 3000);

        service.modify(id, request);

        assertEquals(3000, entity.getExpirationDays());
        assertEquals(3000, entity.getPointAmount());
    }

    @Test
    @DisplayName("modify() 실패 - 존재하지 않은 포인트 정책")
    void modify_fail_1() {
        PointPolicyModifyRequest request = new PointPolicyModifyRequest(3000, 3000);
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            service.modify(1, request);
        });

        assertEquals("포인트 정책이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("toggleActivation() 성공")
    void toggleActivation_success() {
        Integer id = 10;

        PointPolicy entity = PointPolicy.create(PolicyType.BIRTHDAY, 1000, 4000);
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(entity));

        assertTrue(entity.getIsActive());

        service.toggleActivation(id);

        assertFalse(entity.getIsActive());
    }

    @Test
    @DisplayName("toggleActivation() 실패 - 존재하지 않은 포인트 정책")
    void toggleActivation_fail_1() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            service.toggleActivation(1);
        });

        assertEquals("포인트 정책이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("getPointPolicies() 성공")
    void getPointPolicies_success() {
        PointPolicy entity1 = PointPolicy.create(PolicyType.BIRTHDAY, 1000, 4000);
        PointPolicy entity2 = PointPolicy.create(PolicyType.SIGN_UP, 2000, 5000);

        ReflectionTestUtils.setField(entity1, "id", 1);
        ReflectionTestUtils.setField(entity2, "id", 2);

        List<PointPolicy> entityList = List.of(entity1, entity2);

        when(repository.findAll()).thenReturn(entityList);

        PointPolicyResponse response1 = new PointPolicyResponse(1, PolicyType.BIRTHDAY, 1000, true, 4000);
        PointPolicyResponse response2 = new PointPolicyResponse(2, PolicyType.SIGN_UP, 2000, true, 5000);


        List<PointPolicyResponse> expected = List.of(response1, response2);

        List<PointPolicyResponse> result = service.getPointPolicies();

        assertEquals(expected.size(), result.size());
        assertEquals(expected.get(0).id(), result.get(0).id());
        assertEquals(expected.get(1).id(), result.get(1).id());

        assertEquals(expected.get(0).policyType(), result.get(0).policyType());
        assertEquals(expected.get(1).policyType(), result.get(1).policyType());

        assertEquals(expected.get(0).expirationDays(), result.get(0).expirationDays());
        assertEquals(expected.get(1).expirationDays(), result.get(1).expirationDays());

        assertEquals(expected.get(0).pointAmount(), result.get(0).pointAmount());
        assertEquals(expected.get(1).pointAmount(), result.get(1).pointAmount());

        assertEquals(expected.get(0).isActive(), result.get(0).isActive());
        assertEquals(expected.get(1).isActive(), result.get(1).isActive());

    }

}