package com.pointliveyoung.forliveyoung.domain.point.controller;

import com.pointliveyoung.forliveyoung.domain.point.dto.response.UserPointResponse;
import com.pointliveyoung.forliveyoung.domain.point.service.UserPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-points")
public class UserPointController {

    private final UserPointService userPointService;

    @GetMapping
    public ResponseEntity<List<UserPointResponse>> getAllUserPoint(@AuthenticationPrincipal Integer userId) {
        try {
            userPointService.updateExpiredPointsByNow(userId);
        } catch (Exception e) {
            log.warn("만료 보정(updateStatusToExpiredPoints) 실패. userId={}", userId, e);
        }

        List<UserPointResponse> responseList = userPointService.getAllByUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseList);
    }

}
