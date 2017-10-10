package com.nalbam.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = "tokens")
public class TokenServiceImpl implements TokenService {

    @Override
    @Cacheable(key = "'token:'+#token")
    public String findOne(final String token) {
        log.debug("## token : " + token);

        return UUID.randomUUID().toString();
    }

}
