package com.example.toeicquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // 메인 시작 화면 (로그인 후 이동)
    @GetMapping("/start")
    public String startPage() {
        return "start"; // templates/start.html
    }

    // 대기방 페이지
    @GetMapping("/waitingRoom")
    public String waitingRoomPage() {
        return "waitingRoom"; // templates/waitingRoom.html
    }

    // 게임 페이지
    @GetMapping("/gameroom")
    public String gameRoomPage() {
        return "gameroom"; // templates/gameroom.html
    }

    // 랭킹 페이지
    @GetMapping("/ranking")
    public String rankingPage() {
        return "ranking"; // templates/ranking.html
    }
}
