package com.example.toeicquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

/*
com.example.toeicquiz
├── controller       ← API 처리 (ex: UserController)
├── dto              ← 요청/응답 DTO 클래스
├── entity           ← ✅ JPA 엔티티 클래스 (User.java)
├── repository       ← DB 접근 인터페이스 (UserRepository)
├── service          ← 비즈니스 로직 처리 (UserService)
└── security         ← 인증/보안 설정 (SecurityConfig 등)*/

/*
| 파일명                        | 역할                              | 위치            |
		| -------------------------- | ------------------------------- | ------------- |
		| `UserRegisterRequest.java` | 회원가입 요청 DTO                     | `dto/`        |
		| `UserLoginRequest.java`    | 로그인 요청 DTO                      | `dto/`        |
		| `UserRepository.java`      | DB 인터페이스                        | `repository/` |
		| `UserService.java`         | 회원가입/로그인 로직                     | `service/`    |
		| `UserController.java`      | API 처리 (POST /register, /login) | `controller/` |
		| `SecurityConfig.java`      | 인증/보안 설정                        | `security/`   |*/

