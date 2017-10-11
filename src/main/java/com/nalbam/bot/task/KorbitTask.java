package com.nalbam.bot.task;

import com.nalbam.bot.service.KorbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class KorbitTask {

    @Value("${nalbam.korbit.username}")
    private String username;

    @Autowired
    private KorbitService korbitService;

    @Scheduled(fixedRate = 600000)
    public void analyzer() {
        final Map token = this.korbitService.getToken();

        log.info("* analyzer token : {}", token);
    }

}
