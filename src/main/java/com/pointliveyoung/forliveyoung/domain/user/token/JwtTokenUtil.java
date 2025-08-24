package com.pointliveyoung.forliveyoung.domain.user.token;

import com.pointliveyoung.forliveyoung.domain.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenUtil {

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
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {

        } catch (JwtException e) {

        } catch (Exception e) {
        }

        return false;
    }


    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
