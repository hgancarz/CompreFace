package com.exadel.frs.core.trainservice.controller;

import static com.exadel.frs.core.trainservice.system.global.Constants.API_V1;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_V1 + "/hello")
public class HelloWorldController {

    @GetMapping
    @ApiOperation(value = "Returns hello world message")
    public String getHelloWorld() {
        return "hello world";
    }
}
