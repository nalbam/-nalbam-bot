package com.nalbam.bot.service;

import java.util.Map;

public interface KorbitService {

    Map getTicker();

    Map getOrderBook();

    Map getTransactions();

    Map token();

    Map analyzer();

}
