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
                // CORS 기본값, CSRF 비활성화 (JWT 사용이므로)
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                // 세션 사용 안 함 (JWT)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL 접근 제어
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 & 기타 공개 파일
                        .requestMatchers(
                                "/stylesheets/**",
                                "/javascripts/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico",
                                "/socket.io/**"
                        ).permitAll()

                        // 템플릿 뷰(화면) 공개
                        .requestMatchers(
                                "/", "/login", "/signup", "/start", "/waitingroom", "/gameroom", "/ranking"
                        ).permitAll()

                        // 회원가입/로그인/아이디중복확인 API 공개
                        .requestMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/check-duplicate-id").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // (필요시) H2 콘솔 같은 iframe 허용하려면 frameOptions 비활성화
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // JWT 필터 등록 (UsernamePasswordAuthenticationFilter 전에)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
