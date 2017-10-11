package com.nalbam.bot.controller;

import com.nalbam.bot.service.KorbitService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/korbit")
@Api(value = "token", description = "토큰")
public class TokenController {

    @Autowired
    private KorbitService korbitService;

    @GetMapping("/token")
    public Map token() {
        return this.korbitService.getToken();
    }

    @GetMapping("/ticker")
    public Map ticker() {
        return this.korbitService.getTicker();
    }

    @GetMapping("/orderbook")
    public Map orderbook() {
        return this.korbitService.getOrderBook();
    }

    @GetMapping("/transactions")
    public Map transactions() {
        return this.korbitService.getTransactions();
    }

}
