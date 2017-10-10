package com.nalbam.bot.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "home", description = "홈")
public class HomeController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

}
