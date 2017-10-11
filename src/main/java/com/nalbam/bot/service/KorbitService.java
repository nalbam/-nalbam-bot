package com.nalbam.bot.service;

import java.util.Map;

public interface KorbitService {

    Map getToken();

    Map getTicker();

    Map getOrderBook();

    Map getTransactions();

    Map accounts(String token);

    Map balances(String token);

    Map buy(String token, Long amount);

    Map sell(String token, Long amount);

}
