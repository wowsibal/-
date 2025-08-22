package com.example.toeicquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/login"})
    public String login() { return "login"; }

    @GetMapping("/signup")
    public String signup() { return "signup"; }

}
