package com.pointliveyoung.forliveyoung.domain.point.controller;

import com.pointliveyoung.forliveyoung.domain.point.dto.response.UserPointResponse;
import com.pointliveyoung.forliveyoung.domain.point.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-points")
public class UserPointController {

    private final UserPointService userPointService;

    @GetMapping
    public ResponseEntity<List<UserPointResponse>> getAllUserPoint(@AuthenticationPrincipal Integer userId) {
        List<UserPointResponse> responseList = userPointService.getAllByUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseList);
    }

}
