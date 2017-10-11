package com.nalbam.bot.repository;

import java.util.Map;

public interface KorbitRepository {

    Map getToken();

    Map getTicker();

    Map getOrderBook();

    Map getTransactions();

}
