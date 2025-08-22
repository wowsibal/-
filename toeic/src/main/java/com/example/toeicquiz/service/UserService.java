package com.example.toeicquiz.service;

import com.example.toeicquiz.dto.UserRegisterRequest;
import com.example.toeicquiz.dto.UserLoginRequest;
import com.example.toeicquiz.entity.User;
import com.example.toeicquiz.repository.UserRepository;
import com.example.toeicquiz.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 로직
    public void register(UserRegisterRequest request) {
        if (userRepository.existsByUserid(request.getUserid())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUserid(request.getUserid());
        user.setName(request.getName());
        user.setDob(request.getDob());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    // 로그인 성공 시 JWT 토큰 생성
    public String loginAndGetToken(UserLoginRequest request) {
        User user = userRepository.findByUserid(request.getUserid())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.createToken(user.getUserid());
    }

    // 현재 로그인된 사용자 정보 반환
    public User getMyInfo() {
        String userid = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
