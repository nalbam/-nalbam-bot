package com.nalbam.bot.service;

import com.nalbam.bot.repository.KorbitRepository;
import com.nalbam.bot.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class KorbitServiceImpl implements KorbitService {

    @Value("${nalbam.korbit.username}")
    private String username;

    @Value("${nalbam.trade.buy.per}")
    private Float buy_per;

    @Value("${nalbam.trade.buy.krw}")
    private Float buy_krw;

    @Value("${nalbam.trade.sell.per}")
    private Float sell_per;

    @Value("${nalbam.trade.sell.btc}")
    private Float sell_btc;

    @Autowired
    private KorbitRepository korbitRepository;

    @Autowired
    private TokenRepository tokenRepository;

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

    private Map saveToken(final Map token, final Long high, final Long low) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", this.username);
        map.put("token_type", token.get("token_type"));
        map.put("access_token", token.get("access_token"));
        map.put("expires_in", token.get("expires_in"));
        map.put("refresh_token", token.get("refresh_token"));
        map.put("high", high);
        map.put("low", low);

        // 토큰 저장
        this.tokenRepository.setToken(map);

        return map;
    }

    @Override
    public Map token() {
        // 저장된 토큰 조회
        final Map saved = this.tokenRepository.getToken(this.username);

        final Map korbit;

        final Long high;
        final Long low;

        if (saved == null) {
            high = 0L;
            low = 0L;

            // 토큰 발급
            korbit = this.korbitRepository.getToken();
        } else {
            if (saved.get("high") != null) {
                high = Long.parseLong(saved.get("high").toString());
            } else {
                high = 0L;
            }
            if (saved.get("low") != null) {
                low = Long.parseLong(saved.get("low").toString());
            } else {
                low = 0L;
            }

            // 토큰 재발급
            korbit = this.korbitRepository.getToken();
            //korbit = this.korbitRepository.getToken(saved.get("refresh_token").toString());
        }

        if (korbit != null) {
            return saveToken(korbit, high, low);
        }

        return null;
    }

    @Override
    public Map analyzer() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            return null;
        }

        final String accessToken = token.get("access_token").toString();
        Long high = Long.parseLong(token.get("high").toString());
        Long low = Long.parseLong(token.get("low").toString());

        boolean buy = false;
        boolean sell = false;

        // 현재 시세 조회
        final Map ticker = this.korbitRepository.getTicker();

        final Long last = Long.parseLong(ticker.get("last").toString());

        final Float high_low = high - (high * this.sell_per);
        final Float low_high = low + (low * this.sell_per);

        log.info("* korbit ++   : {} ", high);
        log.info("* korbit +    : {} ", high_low);
        log.info("* korbit last : {} ", last);
        log.info("* korbit -    : {} ", low_high);
        log.info("* korbit --   : {} ", low);

        if (high == 0L || high <= last) {
            high = last;
        } else {
            // 팔자
            if (high_low > last) {
                sell = true;

                log.info("* korbit sell : {} > {}", high_low, last);
            }
        }

        if (low == 0L || low >= last) {
            low = last;
        } else {
            // 사자
            if (low_high < last) {
                buy = true;

                log.info("* korbit buy : {} > {}", low_high, last);
            }
        }

        // 기준가 저장 (토큰)
        saveToken(token, high, low);

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        Float krw = Float.parseFloat(((Map) balances.get("krw")).get("available").toString());
        Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        final Map result = null;

        if (sell && btc > 0) {
            if (btc > this.sell_btc) {
                btc = this.sell_btc;
            }

            // TODO 팔자
            //result = this.korbitRepository.sell(accessToken, btc);

            log.info("* korbit sell : {}", result);
        } else if (buy && krw > 0) {
            if (krw > this.buy_krw) {
                krw = this.buy_krw;
            }

            // TODO 사자
            //result = this.korbitRepository.buy(accessToken, krw.longValue());

            log.info("* korbit buy  : {}", result);
        }

        // 결과
        return result;
    }

}
