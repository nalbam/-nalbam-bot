package com.nalbam.bot.service;

import com.nalbam.bot.repository.KorbitRepository;
import com.nalbam.bot.repository.SlackRepository;
import com.nalbam.bot.repository.TokenRepository;
import in.ashwanthkumar.slack.webhook.SlackMessage;
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
    private Long buy_krw;

    @Value("${nalbam.trade.sell.per}")
    private Float sell_per;

    @Value("${nalbam.trade.sell.btc}")
    private Float sell_btc;

    @Autowired
    private KorbitRepository korbitRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SlackRepository slackRepository;

    @Override
    public Map token() {
        // 저장된 토큰 조회
        final Map saved = this.tokenRepository.getToken(this.username);

        final Map korbit;

        final Long nonce;
        final Long high;
        final Long low;

        if (saved == null) {
            nonce = 0L;
            high = 0L;
            low = 0L;

            // 토큰 발급
            korbit = this.korbitRepository.getToken();
        } else {
            nonce = Long.parseLong(saved.get("nonce").toString());
            high = Long.parseLong(saved.get("high").toString());
            low = Long.parseLong(saved.get("low").toString());

            // 토큰 재발급
            korbit = this.korbitRepository.getToken(saved.get("refresh_token").toString());
        }

        if (korbit != null) {
            return saveToken(korbit, nonce, high, low);
        }

        return null;
    }

    @Override
    public Map analyzer() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            log.info("* korbit analyzer : token is null");
            return null;
        }

        final String accessToken = token.get("access_token").toString();
        Long nonce = Long.parseLong(token.get("nonce").toString());
        Long high = Long.parseLong(token.get("high").toString());
        Long low = Long.parseLong(token.get("low").toString());

        boolean buy = false;
        boolean sell = false;

        // 현재 시세 조회
        final Map ticker = this.korbitRepository.getTicker();

        final Long last = Long.parseLong(ticker.get("last").toString());

        final Float high_low = high - (high * this.sell_per);
        final Float low_high = low + (low * this.sell_per);

        if (high == 0 || high <= last) {
            high = last;
        } else {
            // 팔자
            if (high_low > last) {
                sell = true;
            }
        }

        if (low == 0 || low >= last) {
            low = last;
        } else {
            // 사자
            if (low_high < last) {
                buy = true;
            }
        }

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());
        Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        if (sell || buy) {
            log.info("* korbit ----------------------------");
            log.info("* korbit ++   : {} ", high);
            log.info("* korbit +    : {} ", high_low);
            log.info("* korbit last : {} ", last);
            log.info("* korbit -    : {} ", low_high);
            log.info("* korbit --   : {} ", low);
            log.info("* korbit ----------------------------");

            if (sell) {
                log.info("* korbit sell : {} ", true);
            }
            if (buy) {
                log.info("* korbit buy  : {} ", true);
            }

            log.info("* korbit ----------------------------");

            log.info("* korbit krw  : {} ", krw);
            log.info("* korbit btc  : {} ", btc);

            log.info("* korbit ----------------------------");

            //this.slackRepository.send(new SlackMessage().quote("sell " + sell).quote("buy " + buy));
        }

        Map result = null;

        if (sell) {
            if (btc > 0) {
                if (btc > this.sell_btc) {
                    btc = this.sell_btc;
                } else {
                    low = last;
                }

                // 팔자
                result = this.korbitRepository.sell(accessToken, btc, nonce++);

                log.info("* korbit sell : {}", btc);
                log.info("* korbit sell : {}", result);

                //this.slackRepository.send(new SlackMessage().quote("sell").text(result.toString()));

                buy = false;
            }
        }

        if (buy) {
            if (krw > 0) {
                if (krw > this.buy_krw) {
                    krw = this.buy_krw;
                } else {
                    high = last;
                }

                // 사자
                result = this.korbitRepository.buy(accessToken, krw, nonce++);

                log.info("* korbit buy  : {}", krw);
                log.info("* korbit buy  : {}", result);

                //this.slackRepository.send(new SlackMessage().quote("buy").text(result.toString()));
            }
        }

        // 기준가 저장 (토큰)
        saveToken(token, nonce, high, low);

        // 결과
        return result;
    }

    @Override
    public Map balances() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            return null;
        }

        final String accessToken = token.get("access_token").toString();

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        final Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());
        final Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        this.slackRepository.send(new SlackMessage().quote("krw " + krw).quote("btc " + btc));

        return balances;
    }

    @Override
    public Map buy() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            return null;
        }

        final String accessToken = token.get("access_token").toString();
        final Long nonce = Long.parseLong(token.get("nonce").toString());
        final Long high = Long.parseLong(token.get("high").toString());
        final Long low = Long.parseLong(token.get("low").toString());

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());

        Map result = null;

        if (krw > 0) {
            if (krw > this.buy_krw) {
                krw = this.buy_krw;
            }

            // 사자
            result = this.korbitRepository.buy(accessToken, krw, nonce);

            // 기준가 저장 (토큰)
            saveToken(token, nonce, high, low);

            log.info("korbit buy : {}", krw);
            log.info("korbit buy : {}", result);

            this.slackRepository.send(new SlackMessage().quote("buy").text(result.toString()));
        }

        return result;
    }

    @Override
    public Map sell() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            return null;
        }

        final String accessToken = token.get("access_token").toString();
        final Long nonce = Long.parseLong(token.get("nonce").toString());
        final Long high = Long.parseLong(token.get("high").toString());
        final Long low = Long.parseLong(token.get("low").toString());

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        Map result = null;

        if (btc > 0) {
            if (btc > this.sell_btc) {
                btc = this.sell_btc;
            }

            // 팔자
            result = this.korbitRepository.sell(accessToken, btc, nonce);

            // 기준가 저장 (토큰)
            saveToken(token, nonce, high, low);

            log.info("korbit sell : {}", btc);
            log.info("korbit sell : {}", result);

            this.slackRepository.send(new SlackMessage().quote("sell").text(result.toString()));
        }

        return result;
    }

    private Map saveToken(final Map token, final Long nonce, final Long high, final Long low) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", this.username);
        map.put("token_type", token.get("token_type"));
        map.put("access_token", token.get("access_token"));
        map.put("expires_in", token.get("expires_in"));
        map.put("refresh_token", token.get("refresh_token"));
        map.put("nonce", nonce);
        map.put("high", high);
        map.put("low", low);

        // 토큰 저장
        this.tokenRepository.setToken(map);

        return map;
    }

}
