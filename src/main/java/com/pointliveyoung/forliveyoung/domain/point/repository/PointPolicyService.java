package com.pointliveyoung.forliveyoung.domain.point.repository;

import com.pointliveyoung.forliveyoung.domain.point.dto.PointPolicyMapper;
import com.pointliveyoung.forliveyoung.domain.point.dto.request.PointPolicyCreateRequest;
import com.pointliveyoung.forliveyoung.domain.point.dto.request.PointPolicyModifyRequest;
import com.pointliveyoung.forliveyoung.domain.point.dto.response.PointPolicyResponse;
import com.pointliveyoung.forliveyoung.domain.point.entity.PointPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointPolicyService {
    private final PointPolicyRepository pointPolicyRepository;

    @Transactional
    public void create(PointPolicyCreateRequest request) {
        PointPolicy entity = PointPolicyMapper.toEntity(request);

        pointPolicyRepository.save(entity);
    }

    @Transactional
    public void modify(Integer id, PointPolicyModifyRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("포인트 정책이 존재하지 않습니다."));
        Optional.ofNullable(request.name()).ifPresent(pointPolicy::changeName);
        Optional.ofNullable(request.expirationDays()).ifPresent(pointPolicy::changeExpirationDays);
        Optional.ofNullable(request.pointAmount()).ifPresent(pointPolicy::changePointAmount);
    }

    @Transactional
    public boolean toggleActivation(Integer id) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("포인트 정책이 존재하지 않습니다."));

        boolean newActivate = !pointPolicy.getIsActivation();

        pointPolicy.changeIsActivation(newActivate);

        return newActivate;
    }

    @Transactional(readOnly = true)
    public List<PointPolicyResponse> getPointPolicies() {
        List<PointPolicy> pointPolicies = pointPolicyRepository.findAll();

        return PointPolicyMapper.toPointPolicyResponseList(pointPolicies);
    }


}
