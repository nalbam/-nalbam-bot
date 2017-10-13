package com.nalbam.bot.repository;

import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SlackRepositoryImpl implements SlackRepository {

    @Value("${nalbam.slack.webhook}")
    private String webhook;

    @Value("${nalbam.slack.channel}")
    private String channel;

    @Override
    public void send(final SlackAttachment attachment) {
        send(this.channel, attachment);
    }

    @Override
    public void send(final String channel, final SlackAttachment attachment) {
        try {
            new Slack(this.webhook).sendToChannel(channel).push(attachment);
        } catch (final Exception e) {
            log.info("slack send error {}", e.getMessage());
        }
    }

}
