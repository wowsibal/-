package com.example.toeicquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/login"})
    public String login() { return "login"; }

    @GetMapping("/signup")
    public String signup() { return "signup"; }

    @GetMapping("/start")
    public String start() { return "start"; }

    @GetMapping("/waitingRoom")
    public String waitingRoom() { return "waitingRoom"; }

    @GetMapping("/gameroom")
    public String gameroom() { return "gameroom"; }

    @GetMapping("/ranking")
    public String ranking() { return "ranking"; }

}
