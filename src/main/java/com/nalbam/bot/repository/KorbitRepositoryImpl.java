package com.nalbam.bot.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class KorbitRepositoryImpl implements KorbitRepository {

    @Value("${nalbam.korbit.api}")
    private String api;

    @Value("${nalbam.korbit.client_id}")
    private String client_id;

    @Value("${nalbam.korbit.client_secret}")
    private String client_secret;

    @Value("${nalbam.korbit.grant_type}")
    private String grant_type;

    @Value("${nalbam.korbit.username}")
    private String username;

    @Value("${nalbam.korbit.password}")
    private String password;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map getToken() {
        final String url = this.api + "/oauth2/access_token";

        final HttpHeaders headers = new HttpHeaders();

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", this.client_id);
        params.add("client_secret", this.client_secret);
        params.add("grant_type", this.grant_type);
        params.add("username", this.username);
        params.add("password", this.password);

        final HttpEntity<MultiValueMap> entity = new HttpEntity<>(params, headers);
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map getTicker() {
        final String url = this.api + "/ticker/detailed";

        final HttpEntity entity = new HttpEntity<>(new HttpHeaders());
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map getOrderBook() {
        final String url = this.api + "/orderbook";

        final HttpEntity entity = new HttpEntity<>(new HttpHeaders());
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map getTransactions() {
        final String url = this.api + "/transactions";

        final HttpEntity entity = new HttpEntity<>(new HttpHeaders());
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map accounts(final String token) {
        final String url = this.api + "/user/accounts";

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        final HttpEntity<Map> entity = new HttpEntity<>(headers);
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map balances(final String token) {
        final String url = "https://api.korbit.co.kr/v1/user/balances";

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        final HttpEntity<MultiValueMap> entity = new HttpEntity<>(headers);
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map buy(final String token, final Long amount) {
        final String url = "https://api.korbit.co.kr/v1/user/orders/buy";

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("currency_pair", "btc_krw");
        params.add("type", "market");
        params.add("fiat_amount", amount.toString());

        final HttpEntity<MultiValueMap> entity = new HttpEntity<>(params, headers);
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map sell(final String token, final Long amount) {
        final String url = "https://api.korbit.co.kr/v1/user/orders/sell";

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("currency_pair", "btc_krw");
        params.add("type", "market");
        params.add("coin_amount", amount.toString());

        final HttpEntity<MultiValueMap> entity = new HttpEntity<>(params, headers);
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
