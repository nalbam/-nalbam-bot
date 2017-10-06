package com.nalbam.bot.coin.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Transactional
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final List<String> phases = Arrays.asList("default", "dev");
        if (!phases.contains(this.profile)) {
            return;
        }

        log.info("## context refreshed : {} {} : {}", this.name, this.profile, event.toString());
    }

}
