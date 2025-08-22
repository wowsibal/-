package com.example.toeicquiz.controller;

import com.example.toeicquiz.dto.UserLoginRequest;
import com.example.toeicquiz.dto.UserRegisterRequest;
import com.example.toeicquiz.entity.User;
import com.example.toeicquiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 요청 처리
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인 요청 처리 (JWT 토큰 반환)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            String token = userService.loginAndGetToken(request);

            // 응답에 userid도 함께 포함
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userid", request.getUserid());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인된 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }
}
