package com.nalbam.bot.repository;

import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackMessage;

import java.util.List;

public interface SlackRepository {

    void send(SlackMessage message);

    void send(SlackAttachment attachment);

    void send(List<SlackAttachment> attachments);

}
