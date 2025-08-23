package com.pointliveyoung.forliveyoung.domain.user.config;

import com.pointliveyoung.forliveyoung.domain.user.token.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;


// Spring Security의 보안 설정을 커스터마이징하는 클래스
// 세션 기반 인증이 default 이지만 JWT를 사용하기 때문에 토큰을 직접 발급하고 검증하는 로직이 필요 그래서 세션을 비활성화함
@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //cors 설정 -> 프론트엔드 와 API 서버가 다른 도메인 일때 필요
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(false);
                    config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "chatRoomId"));
                    config.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                // JWT 사용할때는 StateLess 환경이라서 CSRF 설정을 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 폼 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // HTTP Basic 인증 비활성화 -> JWT 기반 로그인 API를 직접 만들었으니까 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션을 사용하지 않도록 설정 -> JWT 사용 시 세션을 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 요청에 대한 인증 설정 -> /api/user/** 경로는 인증 없이 접근 허용, 그 외의 모든 요청은 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users", "api/users/login", "/api/users/refresh").permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
