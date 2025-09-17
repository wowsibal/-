package com.example.toeicquiz.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;                       // ✅ 변경
import org.springframework.data.mongodb.core.index.Indexed;   // ✅ 추가
import org.springframework.data.mongodb.core.mapping.Document;// ✅ 추가

@Getter @Setter
@Document(collection = "users") // ✅ Mongo 컬렉션명
public class User {

    @Id                         // ✅ Mongo의 _id
    private String id;          // ✅ Long → String 권장

    @Indexed(unique = true)     // ✅ 고유 인덱스
    private String userid;

    @Indexed(unique = true)     // 필요 없으면 unique 제거 가능
    private String name;

    private String dob;
    private String password;
}
