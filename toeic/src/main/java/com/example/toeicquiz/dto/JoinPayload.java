package com.example.toeicquiz.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @ToString
public class JoinPayload {
    private String roomId; //방 ID
    private String user; //유저 표시명/ 아이디
}
