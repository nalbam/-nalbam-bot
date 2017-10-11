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

}
