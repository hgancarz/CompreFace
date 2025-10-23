package com.exadel.frs.core.trainservice.controller;

import static com.exadel.frs.core.trainservice.system.global.Constants.API_V1;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_V1)
public class HelloWorldController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "hello world";
    }
}
