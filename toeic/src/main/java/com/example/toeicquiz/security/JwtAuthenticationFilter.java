package com.example.toeicquiz.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String header = request.getHeader("Authorization");
            String token = null;

            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }

            // 토큰이 없으면 그냥 통과 (permitAll 경로 보호)
            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰이 있으면 유효성만 체크하고, 유효할 때만 인증 세팅
            if (jwtTokenProvider.validateToken(token)) {
                String userid = jwtTokenProvider.getUserid(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userid, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 유효하지 않은 토큰이어도 여기서 에러 내지 말고 그냥 통과
            // (인증이 필요한 경로에서만 차단됨)

        } catch (Exception ignored) {
            // 여기서 에러를 응답 보내지 말 것. 그대로 통과시켜야 public 경로가 403 안 뜸
        }

        filterChain.doFilter(request, response);
    }
}
