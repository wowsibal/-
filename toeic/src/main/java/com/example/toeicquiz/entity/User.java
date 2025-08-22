package com.example.toeicquiz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 아이디 (중복 불가)
    @Column(nullable = false, unique = true)
    private String userid;

    // 사용자 이름 (중복 불가)
    @Column(nullable = false, unique = true)
    private String name;

    // 생년월일
    @Column(nullable = false)
    private String dob;

    // 비밀번호
    @Column(nullable = false)
    private String password;
}
