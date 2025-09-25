package com.example.toeicquiz.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @ToString
public class Presence {
    private String type; // "join" | "leave" | "start"
    private String user; // 사용자명 or "system"
    private int count;   // 현재 방 인원수(옵션)
}
