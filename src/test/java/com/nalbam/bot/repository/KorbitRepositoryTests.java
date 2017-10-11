package com.nalbam.bot.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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
        log.debug("## korbit username : {}", this.username);

        // get token from korbit
        final Map token = this.korbitRepository.getToken();

        log.debug("## korbit token : {}", token);

        // set token to dynamo
        this.tokenRepository.setToken(this.username, token);

        // get token from dynamo
        final Map saved = this.tokenRepository.getToken(this.username);

        log.debug("## korbit saved : {}", saved);

        // get balance from korbit
        final Map balances = this.korbitRepository.balances(token.get("access_token").toString());

        log.debug("## korbit balances : {}", balances);
    }

}
