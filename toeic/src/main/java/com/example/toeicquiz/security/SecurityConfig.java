// src/main/java/com/example/toeicquiz/security/SecurityConfig.java
package com.example.toeicquiz.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 정적 자원
                        .requestMatchers(
                                "/stylesheets/**", "/javascripts/**", "/images/**",
                                "/webjars/**", "/favicon.ico"
                        ).permitAll()

                        // 페이지(뷰) 경로: GET은 공개
                        .requestMatchers(HttpMethod.GET,
                                "/", "/login", "/signup", "/start",
                                "/waitingRoom", "/gameRoom", "/ranking"
                        ).permitAll()

                        // WebSocket 핸드셰이크
                        .requestMatchers("/ws/**").permitAll()

                        // 회원가입/로그인/아이디중복확인 API 공개
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/register", "/api/users/login"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/check-duplicate-id"
                        ).permitAll()

                        // 나머지 API는 인증 필요
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
