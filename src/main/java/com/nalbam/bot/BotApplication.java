package com.nalbam.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BotApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

}
