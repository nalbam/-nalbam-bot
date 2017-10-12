package com.nalbam.bot.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        final String url = this.api + "/token/" + id;

        final HttpHeaders headers = new HttpHeaders();
        headers.add("x-api-key", this.key);

        try {
            final HttpEntity<Map> entity = new HttpEntity<>(headers);
            final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            final Map map = new ObjectMapper().readValue(response.getBody(), Map.class);
            return (Map) map.get("Item");
        } catch (final Exception e) {
            log.info(e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setToken(final Map params) {
        final String url = this.api + "/token";

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", this.key);

        try {
            final String json = new ObjectMapper().writeValueAsString(params);

            final HttpEntity<String> entity = new HttpEntity<>(json, headers);
            final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (final Exception e) {
            log.info(e.getMessage());
            //e.printStackTrace();
        }
    }

}
