package com.nalbam.bot.task;

import com.nalbam.bot.domain.Queue;
import com.nalbam.bot.service.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CallbackTask {

    @Value("${app.product}")
    private String product;

    @Value("${app.profile}")
    private String profile;

    @Value("${aws.region}")
    private String region;

    @Autowired
    private SendService sendService;

    @Scheduled(fixedRate = 1000)
    public void callback() {
        Map<String, String> data = new HashMap<>();
        data.put("url", "http://" + product + "-" + profile + "." + region + ".elasticbeanstalk.com/health");

        Queue queue = new Queue();
        queue.setType('2');
        queue.setDelay(0);
        queue.setData(data);
        queue.setTokens(new ArrayList<>());
        queue.setRegistered(new Date());

        // 발송
        this.sendService.send(queue);
    }

}
