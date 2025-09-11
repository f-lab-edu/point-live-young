package com.pointliveyoung.forliveyoung.domain.user.service;

import com.pointliveyoung.forliveyoung.domain.point.event.PointEvent;
import com.pointliveyoung.forliveyoung.domain.user.dto.response.AuthTokens;
import com.pointliveyoung.forliveyoung.domain.user.token.JwtTokenUtil;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.LoginRequest;
import com.pointliveyoung.forliveyoung.domain.user.dto.request.UserCreateRequest;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void signUp(UserCreateRequest request) {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        String encodePassword = passwordEncoder.encode(request.getPassword());

        User saveUser = userRepository.save(User.of(request.getName(), request.getEmail(), encodePassword, request.getBirthDate()));

        eventPublisher.publishEvent(new PointEvent(saveUser));
    }

    @Transactional
    public AuthTokens login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 이메일의 사용자가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new SecurityException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUserRole());
        user.changeRefreshToken(refreshToken);

        eventPublisher.publishEvent(new PointEvent(user));

        user.recordLogin(LocalDateTime.now());


        return new AuthTokens(accessToken, refreshToken);
    }

    @Transactional
    public AuthTokens renewTokens(String refreshToken) {
        if (Objects.isNull(refreshToken) || !jwtTokenUtil.validateToken(refreshToken)) {
            throw new SecurityException("유효하지 않은 Refresh Token 입니다.");
        }

        String userId = jwtTokenUtil.getUserId(refreshToken);
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new SecurityException("Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUserRole());
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUserRole());

        user.changeRefreshToken(newRefreshToken);

        return new AuthTokens(newAccessToken, newRefreshToken);
    }


    @Transactional
    public void logout(String refreshToken) {
        if (Objects.isNull(refreshToken) || !jwtTokenUtil.validateToken(refreshToken)) {
            return;
        }

        String userId = jwtTokenUtil.getUserId(refreshToken);
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

        user.invalidateRefreshToken();
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    @Transactional(readOnly = true)
    public void checkExistUserById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 사용자가 없습니다.");
        }
    }


}
