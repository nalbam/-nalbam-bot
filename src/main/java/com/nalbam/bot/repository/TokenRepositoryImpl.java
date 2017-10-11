package com.nalbam.bot.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TokenRepositoryImpl implements TokenRepository {

    @Value("${nalbam.aws.api}")
    private String api;

    @Value("${nalbam.aws.key}")
    private String key;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map getToken(final String id) {
        final String url = this.api + "/" + id;

        final HttpHeaders headers = new HttpHeaders();
        headers.add("x-api-key", this.key);

        final HttpEntity<Map> entity = new HttpEntity<>(headers);
        final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            final Map map = new ObjectMapper().readValue(response.getBody(), Map.class);
            return (Map) map.get("Item");
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setToken(final String id, final Map token) {
        final String url = this.api;

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", this.key);

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("token_type", token.get("token_type"));
        params.put("access_token", token.get("access_token"));
        params.put("expires_in", token.get("expires_in"));
        params.put("refresh_token", token.get("refresh_token"));

        try {
            final String json = new ObjectMapper().writeValueAsString(params);

            final HttpEntity<String> entity = new HttpEntity<>(json, headers);
            final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
