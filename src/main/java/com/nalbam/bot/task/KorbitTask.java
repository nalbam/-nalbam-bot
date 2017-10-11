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

    @Scheduled(fixedRate = 1800000)
    public void token() {
        final Map token = this.korbitService.token();

        log.info("# korbit task token : {}", token);
    }

    @Scheduled(fixedRate = 60000)
    public void analyzer() {
        log.info("# korbit ----------------------------");

        final Map analyzer = this.korbitService.analyzer();

        log.info("# korbit task analyzer : {}", analyzer);
    }

}
