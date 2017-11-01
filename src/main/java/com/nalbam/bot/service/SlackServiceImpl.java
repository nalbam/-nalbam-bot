package com.nalbam.bot.service;

import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class SlackServiceImpl implements SlackService {

    @Value("${nalbam.slack.webhook}")
    private String webhook;

    @Value("${nalbam.slack.channel}")
    private String channel;

    @Override
    public void send(final String text) {
        send(new SlackMessage().text(text));
    }

    @Override
    public void send(final SlackMessage message) {
        try {
            new Slack(this.webhook).sendToChannel(this.channel).push(message);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(final SlackAttachment attachment) {
        try {
            new Slack(this.webhook).sendToChannel(this.channel).push(attachment);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(final List<SlackAttachment> attachments) {
        try {
            new Slack(this.webhook).sendToChannel(this.channel).push(attachments);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
