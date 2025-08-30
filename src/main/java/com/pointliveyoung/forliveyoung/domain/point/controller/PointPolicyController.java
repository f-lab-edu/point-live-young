package com.pointliveyoung.forliveyoung.domain.point.controller;

import com.pointliveyoung.forliveyoung.domain.point.dto.request.PointPolicyCreateRequest;
import com.pointliveyoung.forliveyoung.domain.point.dto.request.PointPolicyModifyRequest;
import com.pointliveyoung.forliveyoung.domain.point.dto.response.PointPolicyResponse;
import com.pointliveyoung.forliveyoung.domain.point.repository.PointPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point-policies")
public class PointPolicyController {
    private final PointPolicyService pointPolicyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody PointPolicyCreateRequest request) {
        pointPolicyService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Integer id,
                                       @Valid @RequestBody PointPolicyModifyRequest request) {
        pointPolicyService.modify(id, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/{id}/toggle-activation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> activate(@PathVariable Integer id) {
        boolean newActivate = pointPolicyService.toggleActivation(id);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newActivate);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PointPolicyResponse>> getPointPolicyAll() {
        List<PointPolicyResponse> pointPolicies = pointPolicyService.getPointPolicies();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pointPolicies);
    }


}
