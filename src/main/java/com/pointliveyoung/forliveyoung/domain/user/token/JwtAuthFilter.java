package com.pointliveyoung.forliveyoung.domain.user.token;

import com.pointliveyoung.forliveyoung.domain.user.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
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
                UserRole userRole = jwtTokenUtil.getUserRole(token);

                List<SimpleGrantedAuthority> simpleGrantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, simpleGrantedAuthorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }


        }
        filterChain.doFilter(request, response);
    }
}
