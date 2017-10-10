package com.nalbam.bot.controller;

import com.nalbam.bot.service.TokenService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/token")
@Api(value = "token", description = "토큰")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public String token() {
        final String token = "1";
        return this.tokenService.findOne(token);
    }

}
