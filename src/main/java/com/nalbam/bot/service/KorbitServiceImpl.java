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

        final String accessToken = token.get("access_token").toString();
        final Long nonce = Long.parseLong(token.get("nonce").toString());
        Long high = Long.parseLong(token.get("high").toString());
        Long low = Long.parseLong(token.get("low").toString());
        Long sell = Long.parseLong(token.get("sell").toString());
        Long buy = Long.parseLong(token.get("buy").toString());

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

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        final Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());
        final Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        if (sell > 3 || buy > 3) {
            log.info("* korbit ----------------------------");
            log.info("* korbit ++   : {} ", high);
            log.info("* korbit +    : {} ", high_low);
            log.info("* korbit last : {} ", last);
            log.info("* korbit -    : {} ", low_high);
            log.info("* korbit --   : {} ", low);
            log.info("* korbit ----------------------------");
            log.info("* korbit sell : {} ", sell);
            log.info("* korbit buy  : {} ", buy);
            log.info("* korbit ----------------------------");
            log.info("* korbit krw  : {} ", krw);
            log.info("* korbit btc  : {} ", btc);

            log.info("* korbit ----------------------------");

            //this.slackRepository.send(new SlackMessage().quote("sell " + sell).quote("buy " + buy));
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

        if (sell > 3 || buy > 3) {
            final String accessToken = token.get("access_token").toString();

            // 코빗 잔액 조회
            final Map balances = this.korbitRepository.balances(accessToken);

            if (sell > buy) {
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
        final Long high = Long.parseLong(token.get("high").toString());
        final Long low = Long.parseLong(token.get("low").toString());

        final Float high_low = high - (high * this.sell_per);
        final Float low_high = low + (low * this.sell_per);

        // 코빗 잔액 조회
        final Map balances = this.korbitRepository.balances(accessToken);

        final Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());
        final Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        // 현재 시세 조회
        final Map ticker = this.korbitRepository.getTicker();

        final Long last = Long.parseLong(ticker.get("last").toString());

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

    private Map buy(final Map token, final Map balances) {
        final String accessToken = token.get("access_token").toString();

        Long krw = Long.parseLong(((Map) balances.get("krw")).get("available").toString());

        Map result = null;

        if (krw > 0) {
            Long nonce = Long.parseLong(token.get("nonce").toString());

            if (krw > this.buy_krw) {
                krw = this.buy_krw;
            }

            // 사자
            result = this.korbitRepository.buy(accessToken, krw, nonce++);

            log.info("korbit buy : {}", krw);
            log.info("korbit buy : {}", result);

            if (result != null) {
                // 기준가 저장 (토큰)
                saveToken(token, nonce);
            }

            final SlackAttachment attachment = new SlackAttachment("");
            attachment.addField(new SlackAttachment.Field("buy", krw.toString(), true));
            if (result != null) {
                attachment.addField(new SlackAttachment.Field("last", token.get("last").toString(), true));
            }
            this.slackRepository.send(attachment);
        }

        return result;
    }

    private Map sell(final Map token, final Map balances) {
        final String accessToken = token.get("access_token").toString();

        Float btc = Float.parseFloat(((Map) balances.get("btc")).get("available").toString());

        Map result = null;

        if (btc > 0) {
            Long nonce = Long.parseLong(token.get("nonce").toString());

            if (btc > this.sell_btc) {
                btc = this.sell_btc;
            }

            // 팔자
            result = this.korbitRepository.sell(accessToken, btc, nonce++);

            log.info("korbit sell : {}", btc);
            log.info("korbit sell : {}", result);

            if (result != null) {
                // 기준가 저장 (토큰)
                saveToken(token, nonce);
            }

            final SlackAttachment attachment = new SlackAttachment("");
            attachment.addField(new SlackAttachment.Field("sell", btc.toString(), true));
            if (result != null) {
                attachment.addField(new SlackAttachment.Field("last", token.get("last").toString(), true));
            }
            this.slackRepository.send(attachment);
        }

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
        map.put("high", 0);
        map.put("low", 0);
        map.put("sell", 0);
        map.put("buy", 0);

        // 토큰 저장
        this.tokenRepository.setToken(map);
    }

}
