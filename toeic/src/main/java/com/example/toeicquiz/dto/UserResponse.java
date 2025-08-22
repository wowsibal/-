//유저 응답 DTO
package com.example.toeicquiz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private String userid;
    private String name;
    private String dob;
}
