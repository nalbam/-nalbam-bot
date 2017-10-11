package com.nalbam.bot.repository;

import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SlackRepositoryImpl implements SlackRepository {

    @Value("${nalbam.slack.webhook}")
    private String webhook;

    @Value("${nalbam.slack.channel}")
    private String channel;

    @Async
    @Override
    public void send(final SlackMessage message) {
        try {
            new Slack(this.webhook).sendToChannel(this.channel).push(message);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
