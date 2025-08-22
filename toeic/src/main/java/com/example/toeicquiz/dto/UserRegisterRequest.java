//회원가입 요청 DTO
package com.example.toeicquiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRegisterRequest {

    private String userid;     // 사용자 ID
    private String name;       // 사용자 이름
    private String dob;        // 생년월일
    private String password;   // 비밀번호
}
