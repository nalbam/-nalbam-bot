package com.nalbam.bot.repository;

import java.util.Map;

public interface KorbitRepository {

    Map getToken();

    Map getToken(String token);

    Map getTicker();

    Map getOrderBook();

    Map getTransactions();

    Map balances(String token);

    Map buy(String token, Long amount);

    Map sell(String token, Float amount);

}
