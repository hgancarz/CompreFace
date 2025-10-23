package com.exadel.frs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/admin/hello")
    public String getHelloWorld() {
        return "hello world";
    }
}
