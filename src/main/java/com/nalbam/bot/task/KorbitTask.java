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

    @Scheduled(cron = "${nalbam.task.token}")
    public void token() {
        final Map result = this.korbitService.token();

        log.info("# korbit task token : {}", result);
    }

    @Scheduled(cron = "${nalbam.task.analyzer")
    public void analyzer() {
        final Map result = this.korbitService.analyzer();

        log.info("# korbit task analyzer : {}", result);
    }

    @Scheduled(cron = "${nalbam.task.trade")
    public void trade() {
        final Map result = this.korbitService.trade();

        log.info("# korbit task trade : {}", result);
    }

    @Scheduled(cron = "${nalbam.task.balances")
    public void balances() {
        final Map result = this.korbitService.balances();

        log.info("# korbit task balances : {}", result);
    }

}
