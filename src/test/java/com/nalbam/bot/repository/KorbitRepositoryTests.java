package com.nalbam.bot.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class KorbitRepositoryTests {

    @Value("${nalbam.korbit.username}")
    private String username;

    @Autowired
    private KorbitRepository korbitRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    public void token() throws Exception {
        final String username = "test@" + this.username;

        log.debug("## korbit username : {}", username);

        // get token from korbit
        final Map token = this.korbitRepository.getToken();

        log.debug("## korbit token : {}", token);

        final Map<String, Object> map = new HashMap<>();
        map.put("id", username);
        map.put("token_type", token.get("token_type"));
        map.put("access_token", token.get("access_token"));
        map.put("expires_in", token.get("expires_in"));
        map.put("refresh_token", token.get("refresh_token"));
        map.put("high", 0);
        map.put("low", 0);

        // set token to aws
        this.tokenRepository.setToken(map);

        log.debug("## korbit save : {}", map);

        // get token from aws
        final Map saved = this.tokenRepository.getToken(username);

        log.debug("## korbit saved : {}", saved);

        // get balance from korbit
        final Map balances = this.korbitRepository.balances(token.get("access_token").toString());

        log.debug("## korbit balances : {}", balances);
    }

}
