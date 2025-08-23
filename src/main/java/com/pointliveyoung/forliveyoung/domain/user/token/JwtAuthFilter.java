package com.pointliveyoung.forliveyoung.domain.user.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


// Authorization 헤더에서 JWT 추출, 유효성 검증
// Spring security는 기본적으로 세션 기반 인증을 사용하는데 JWT 쓰면 사용자가 보낸 토큰을 검증할 로직이 필요하다
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter : 모든 요청에 대해 단 한 번만 실행되는 필터를 만들기 위한 추상 클래스
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtTokenUtil.validateToken(token)) {
                String userId = jwtTokenUtil.getUserId(token);

                UsernamePasswordAuthenticationToken authentication = //Spring Security 에서 인증 정보를 나타내는 객체
                        new UsernamePasswordAuthenticationToken(userId, null, List.of());

                SecurityContextHolder.getContext().setAuthentication(authentication); //SecurityContextHolder : 보안 컨텍스트를 저장하고 제공하는 역할,그래서 사용자 정보 조회가 가능하다.
            }


        }
        filterChain.doFilter(request, response);
    }
}
