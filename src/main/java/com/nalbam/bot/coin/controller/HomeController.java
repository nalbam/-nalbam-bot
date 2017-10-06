package com.nalbam.bot.coin.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(value = "home", description = "홈")
public class HomeController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

}
