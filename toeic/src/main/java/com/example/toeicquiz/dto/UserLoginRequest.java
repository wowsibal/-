//로그인 요청 DTO
package com.example.toeicquiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginRequest {

    private String userid;     // 사용자 ID
    private String password;   // 비밀번호
}
