package com.nalbam.bot.service;

import com.nalbam.bot.repository.KorbitRepository;
import com.nalbam.bot.repository.SlackRepository;
import com.nalbam.bot.repository.TokenRepository;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KorbitServiceImpl implements KorbitService {

    @Value("${korbit.username}")
    private String username;

    @Value("${trade.sell.per}")
    private Float sell_per;

    @Value("${trade.sell.amt}")
    private Float sell_amt;

    @Value("${trade.sell.min}")
    private Float sell_min;

    @Value("${trade.sell.sgn}")
    private Long sell_sgn;

    @Value("${trade.buy.per}")
    private Float buy_per;

    @Value("${trade.buy.amt}")
    private Long buy_amt;

    @Value("${trade.buy.min}")
    private Long buy_min;

    @Value("${trade.buy.sgn}")
    private Long buy_sgn;

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

        final Map token;
        final Map<String, Object> korbit = new HashMap<>();

        if (saved == null) {
            // 토큰 발급
            token = this.korbitRepository.getToken();

            korbit.put("nonce", 0L);
            korbit.put("last", 0L);
            korbit.put("high", 0L);
            korbit.put("low", 0L);
            korbit.put("sell", 0L);
            korbit.put("buy", 0L);
        } else {
            // 토큰 재발급
            token = this.korbitRepository.getToken(saved.get("refresh_token").toString());

            korbit.put("nonce", saved.get("nonce"));
            korbit.put("last", saved.get("last"));
            korbit.put("high", saved.get("high"));
            korbit.put("low", saved.get("low"));
            korbit.put("sell", saved.get("sell"));
            korbit.put("buy", saved.get("buy"));
        }

        if (token != null) {
            return saveToken(token, korbit);
        }

        return null;
    }

    @Override
    public Map reset() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            log.info("* korbit reset : token is null");
            return null;
        }

        final Map<String, Object> korbit = new HashMap<>();
        korbit.put("nonce", token.get("nonce"));
        korbit.put("last", 0L);
        korbit.put("high", 0L);
        korbit.put("low", 0L);
        korbit.put("sell", 0L);
        korbit.put("buy", 0L);

        return saveToken(token, korbit);
    }

    @Override
    public Map analyzer() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            log.info("* korbit analyzer : token is null");
            return null;
        }

        final Long nonce = Long.parseLong(token.get("nonce").toString());
        Long high = Long.parseLong(token.get("high").toString());
        Long low = Long.parseLong(token.get("low").toString());
        Long sell = Long.parseLong(token.get("sell").toString());
        Long buy = Long.parseLong(token.get("buy").toString());

        // 최종 체결 가격
        final Long last = getLastPrice();

        final Float high_low = high - (high * this.sell_per);
        final Float low_high = low + (low * this.sell_per);

        if (high == 0 || high <= last) {
            high = last;
        } else {
            // 팔자
            if (high_low > last) {
                sell++;
            }
        }

        if (low == 0 || low >= last) {
            low = last;
        } else {
            // 사자
            if (low_high < last) {
                buy++;
            }
        }

        final Map<String, Object> korbit = new HashMap<>();
        korbit.put("nonce", nonce);
        korbit.put("last", last);
        korbit.put("high", high);
        korbit.put("low", low);
        korbit.put("sell", sell);
        korbit.put("buy", buy);

        // 기준가 저장 (토큰)
        saveToken(token, korbit);

        if (sell > this.sell_sgn || buy > this.buy_sgn) {
            final SlackAttachment attachment = new SlackAttachment("");
            attachment.addField(new SlackAttachment.Field("sell", sell.toString(), true));
            attachment.addField(new SlackAttachment.Field("buy", buy.toString(), true));
            attachment.addField(new SlackAttachment.Field("high", high + " (" + high_low + ")", true));
            attachment.addField(new SlackAttachment.Field("low", low + " (" + low_high + ")", true));
            attachment.addField(new SlackAttachment.Field("last", last.toString(), true));
            this.slackRepository.send(attachment);
        }

        // 결과
        return korbit;
    }

    @Override
    public Map trade() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            log.info("* korbit trade : token is null");
            return null;
        }

        final Long sell = Long.parseLong(token.get("sell").toString());
        final Long buy = Long.parseLong(token.get("buy").toString());

        Map result = null;

        if (sell > this.sell_sgn || buy > this.buy_sgn) {
            final String accessToken = token.get("access_token").toString();

            // 코빗 잔액 조회
            final Map balances = this.korbitRepository.balances(accessToken);

            if (sell >= buy) {
                result = sell(token, balances);
            } else {
                result = buy(token, balances);
            }
        }

        return result;
    }

    @Override
    public Map balances() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            log.info("* korbit balances : token is null");
            return null;
        }

        final String accessToken = token.get("access_token").toString();
        final Long last = Long.parseLong(token.get("last").toString());
        final Long high = Long.parseLong(token.get("high").toString());
        final Long low = Long.parseLong(token.get("low").toString());

        final Float high_low = high - (high * this.sell_per);
        final Float low_high = low + (low * this.sell_per);

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        final Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());
        final Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        // 내 잔액
        final Float total = krw + (btc * last);

        final SlackAttachment attachment = new SlackAttachment("");
        attachment.addField(new SlackAttachment.Field("krw", krw.toString(), true));
        attachment.addField(new SlackAttachment.Field("btc", btc.toString(), true));
        attachment.addField(new SlackAttachment.Field("high", high + " (" + high_low + ")", true));
        attachment.addField(new SlackAttachment.Field("low", low + " (" + low_high + ")", true));
        attachment.addField(new SlackAttachment.Field("last", last.toString(), true));
        attachment.addField(new SlackAttachment.Field("total", total.toString(), true));
        this.slackRepository.send(attachment);

        return balances;
    }

    @Override
    public Map buy() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            log.info("* korbit buy : token is null");
            return null;
        }

        final String accessToken = token.get("access_token").toString();

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        return buy(token, balances);
    }

    @Override
    public Map sell() {
        // 코빗 토큰 조회
        final Map token = this.tokenRepository.getToken(this.username);

        if (token == null) {
            log.info("* korbit sell : token is null");
            return null;
        }

        final String accessToken = token.get("access_token").toString();

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        return sell(token, balances);
    }

    private Long getLastPrice() {
        // 최종 체결 가격
        //final Map ticker = this.korbitRepository.getTicker();
        //return Long.parseLong(ticker.get("last").toString());

        // 체결 내역
        final List transactions = this.korbitRepository.getTransactions();
        Map map;
        Long price = 0L;
        Long count = 0L;
        for (final Object o : transactions) {
            map = (Map) o;

            price = price + Long.parseLong(map.get("price").toString());
            count++;
        }
        return price / count;
    }

    private Map buy(final Map token, final Map balances) {
        final String accessToken = token.get("access_token").toString();

        Long nonce = Long.parseLong(token.get("nonce").toString());

        Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());

        Map result = null;
        final String status;

        if (krw > this.buy_min) {
            if (krw > (this.buy_amt + this.buy_min)) {
                krw = this.buy_amt;
            }

            // 사자
            result = this.korbitRepository.buy(accessToken, krw, nonce++);

            log.info("korbit buy : {}", krw);
            log.info("korbit buy : {}", result);

            if (result != null) {
                status = "success";
            } else {
                status = "failure";
            }
        } else {
            status = "not_enough";
        }

        // 기준가 저장 (토큰)
        saveToken(token, nonce);

        final SlackAttachment attachment = new SlackAttachment("");
        attachment.addField(new SlackAttachment.Field("buy", krw.toString(), true));
        attachment.addField(new SlackAttachment.Field("status", status, true));
        this.slackRepository.send(attachment);

        return result;
    }

    private Map sell(final Map token, final Map balances) {
        final String accessToken = token.get("access_token").toString();

        Long nonce = Long.parseLong(token.get("nonce").toString());

        Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        Map result = null;
        final String status;

        if (btc > this.sell_min) {
            if (btc > (this.sell_amt + this.sell_min)) {
                btc = this.sell_amt;
            }

            // 팔자
            result = this.korbitRepository.sell(accessToken, btc, nonce++);

            log.info("korbit sell : {}", btc);
            log.info("korbit sell : {}", result);

            if (result != null) {
                status = "success";
            } else {
                status = "failure";
            }
        } else {
            status = "not_enough";
        }

        // 기준가 저장 (토큰)
        saveToken(token, nonce);

        final SlackAttachment attachment = new SlackAttachment("");
        attachment.addField(new SlackAttachment.Field("sell", btc.toString(), true));
        attachment.addField(new SlackAttachment.Field("status", status, true));
        this.slackRepository.send(attachment);

        return result;
    }

    private Map saveToken(final Map token, final Map<String, Object> korbit) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", this.username);
        map.put("token_type", token.get("token_type"));
        map.put("access_token", token.get("access_token"));
        map.put("expires_in", token.get("expires_in"));
        map.put("refresh_token", token.get("refresh_token"));
        map.put("nonce", korbit.get("nonce"));
        map.put("last", korbit.get("last"));
        map.put("high", korbit.get("high"));
        map.put("low", korbit.get("low"));
        map.put("sell", korbit.get("sell"));
        map.put("buy", korbit.get("buy"));

        // 토큰 저장
        this.tokenRepository.setToken(map);

        return korbit;
    }

    private void saveToken(final Map token, final Long nonce) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", this.username);
        map.put("token_type", token.get("token_type"));
        map.put("access_token", token.get("access_token"));
        map.put("expires_in", token.get("expires_in"));
        map.put("refresh_token", token.get("refresh_token"));
        map.put("nonce", nonce);
        map.put("last", token.get("last"));
        map.put("high", token.get("last"));
        map.put("low", token.get("last"));
        map.put("sell", 0);
        map.put("buy", 0);

        // 토큰 저장
        this.tokenRepository.setToken(map);
    }

}
