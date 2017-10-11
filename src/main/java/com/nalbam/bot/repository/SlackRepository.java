package com.nalbam.bot.repository;

import in.ashwanthkumar.slack.webhook.SlackMessage;

public interface SlackRepository {

    void send(SlackMessage message);

}
