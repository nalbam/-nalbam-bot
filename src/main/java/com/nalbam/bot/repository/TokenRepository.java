package com.nalbam.bot.repository;

import java.util.Map;

public interface TokenRepository {

    Map getToken(String id);

    void setToken(String id, Map token);

}
