package com.nalbam.bot.coin.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class TokenTask {

    @Transactional
    @Scheduled(fixedRate = 600000)
    public void clean() {
        final Calendar now = Calendar.getInstance();
        final Date expire = now.getTime();

        log.info("* token clean : {}", expire);
    }

}
