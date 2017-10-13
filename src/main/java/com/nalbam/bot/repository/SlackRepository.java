package com.nalbam.bot.repository;

import in.ashwanthkumar.slack.webhook.SlackAttachment;

public interface SlackRepository {

    void send(SlackAttachment attachment);

    void send(String channel, SlackAttachment attachment);

}
