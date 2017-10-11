package com.nalbam.bot.service;

import java.util.Map;

public interface KorbitService {

    Map getToken();

    Map getTicker();

    Map getOrderBook();

    Map getTransactions();

}
