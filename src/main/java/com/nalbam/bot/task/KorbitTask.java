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

    // 30분
    @Scheduled(fixedRate = 1802000)
    public void token() {
        final Map result = this.korbitService.token();

        log.info("# korbit task token : {}", result);
    }

    // 3분
    @Scheduled(fixedRate = 180000)
    public void analyzer() {
        final Map result = this.korbitService.analyzer();

        log.info("# korbit task analyzer : {}", result);
    }

    // 60분
    @Scheduled(fixedRate = 3600000)
    public void balances() {
        final Map result = this.korbitService.balances();

        log.info("# korbit task balances : {}", result);
    }

}
