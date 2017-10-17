package com.nalbam.bot.controller;

import com.nalbam.bot.service.KorbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/korbit")
public class KorbitController {

    @Autowired
    private KorbitService korbitService;

    @GetMapping("/token")
    public Map token() {
        return this.korbitService.token();
    }

    @GetMapping("/reset")
    public Map reset() {
        return this.korbitService.reset();
    }

    @GetMapping("/buy")
    public Map buy() {
        return this.korbitService.buy();
    }

    @GetMapping("/sell")
    public Map sell() {
        return this.korbitService.sell();
    }

}
