package com.exadel.frs.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/hello")
    @ApiOperation(value = "Return hello world message")
    public String helloWorld() {
        return "hello world";
    }
}
