package com.Xandred.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HomeController {

    @GetMapping
    public String home(){
        return "Welcome to Revenex";
    }

    @GetMapping("/api")
    public String secure(){
        return "Welcome to Revenex";
    }
}


