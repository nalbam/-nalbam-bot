package com.nalbam.bot.listener;

import com.nalbam.bot.service.SlackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${nalbam.app.name}")
    private String name;

    @Autowired
    private SlackService slackService;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        log.info("## context refreshed : {}", this.name);

        this.slackService.send("Context refreshed : [" + this.name + "]");
    }

}
