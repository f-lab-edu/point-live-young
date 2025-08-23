package com.pointliveyoung.forliveyoung.domain.user.token;

import com.pointliveyoung.forliveyoung.domain.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


//JWT 생성 & 검증 담당
// Spring security 는 기본적으로 세션 기반으로 동작하지만 JWT를 사용하기 대문에 토큰을 직접 발급하고 검증하는 유틸리티가 필요
@Component
public class JwtTokenUtil {

    // JWT 토큰을 서명하고 검증하는데 쓰는 비밀키
    // 이키가 있어야 토큰을 발급할수 있고 서버가 토큰 위변조 여부를 확인할 수 있음
    // Signature 를 만들때 이 secretKey를 사용해서 HMAC SHA256 알고리즘으로 서명
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-seconds}")
    private long accessTokenSeconds;

    @Value("${jwt.refresh-token-seconds}")
    private long refreshTokenSeconds;


    public String generateAccessToken(int userId, UserRole role) {
        return createToken(userId, accessTokenSeconds, role);
    }

    public String generateRefreshToken(int userId, UserRole role) {
        return createToken(userId, refreshTokenSeconds, role);
    }


    private String createToken(int userId, long validity, UserRole role) {
        Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(userId));
        claims.put("role", role);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + (validity * 1000));

        // Payload + Header + Signature 를 이용해서 HMAC-SHA256 알고리즘으로 서명
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserId(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public boolean validateToken(String token) {
        try {
            // 서버가 요청해서 받은 토큰을 검증할때도 secretKey로 서명해서 위변조 여부를 확인
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
        } catch (JwtException e) {
            // 토큰이 변조되었거나 잘못된 경우
        } catch (Exception e) {
            // 기타 예외 처리
        }

        return false;
    }

    // Claims는 JWT의 Payload 부분에 해당하는 정보를 담은 객체
    // 아래 메서드는 Claims 객체를 반환
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // secretKey를 이용해서 서명키 생성, 바이트 배열로 변환후에 Key 객체 생성
    // HS256 알고리즘에서 사용할 수 있는 형태로 변환하는 과정
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
