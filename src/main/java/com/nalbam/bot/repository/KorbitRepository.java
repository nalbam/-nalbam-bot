package com.nalbam.bot.repository;

import java.util.Map;

public interface KorbitRepository {

    Map getToken();

    Map getToken(String token);

    Map getTicker();

    Map balances(String token);

    Map buy(String token, Long amount, Long nonce);

    Map sell(String token, Float amount, Long nonce);

}
