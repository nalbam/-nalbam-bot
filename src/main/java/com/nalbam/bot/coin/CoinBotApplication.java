package com.nalbam.bot.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CoinBotApplication {

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(CoinBotApplication.class, args);
    }

}
