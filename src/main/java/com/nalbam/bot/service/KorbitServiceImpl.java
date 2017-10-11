package com.nalbam.bot.service;

import com.nalbam.bot.repository.KorbitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class KorbitServiceImpl implements KorbitService {

    @Autowired
    private KorbitRepository korbitRepository;

    @Override
    public Map getToken() {
        return this.korbitRepository.getToken();
    }

    @Override
    public Map getTicker() {
        return this.korbitRepository.getTicker();
    }

    @Override
    public Map getOrderBook() {
        return this.korbitRepository.getOrderBook();
    }

    @Override
    public Map getTransactions() {
        return this.korbitRepository.getTransactions();
    }

    @Override
    public Map accounts(final String token) {
        return this.korbitRepository.accounts(token);
    }

    @Override
    public Map balances(final String token) {
        return this.korbitRepository.balances(token);
    }

    @Override
    public Map buy(final String token, final Long amount) {
        return this.korbitRepository.buy(token, amount);
    }

    @Override
    public Map sell(final String token, final Long amount) {
        return this.korbitRepository.sell(token, amount);
    }

}
