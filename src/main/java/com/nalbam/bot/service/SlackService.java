package com.nalbam.bot.service;

import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackMessage;

import java.util.List;

public interface SlackService {

    void send(String text);

    void send(SlackMessage message);

    void send(SlackAttachment attachment);

    void send(List<SlackAttachment> attachments);

}
