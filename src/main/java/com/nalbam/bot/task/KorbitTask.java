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

    @Value("${nalbam.korbit.enabled}")
    private Boolean enabled;

    @Autowired
    private KorbitService korbitService;

    @Scheduled(cron = "00 */30 * * * *")
    public void token() {
        if (!this.enabled) {
            return;
        }

        final Map result = this.korbitService.token();

        log.info("# korbit task token : {}", result);
    }

    @Scheduled(cron = "15 * * * * *")
    public void analyzer() {
        if (!this.enabled) {
            return;
        }

        final Map result = this.korbitService.analyzer();

        log.info("# korbit task analyzer : {}", result);
    }

    @Scheduled(cron = "30 */5 * * * *")
    public void trade() {
        if (!this.enabled) {
            return;
        }

        final Map result = this.korbitService.trade();

        log.info("# korbit task trade : {}", result);
    }

    @Scheduled(cron = "45 */10 * * * *")
    public void balances() {
        if (!this.enabled) {
            return;
        }

        final Map result = this.korbitService.balances();

        log.info("# korbit task balances : {}", result);
    }

}
