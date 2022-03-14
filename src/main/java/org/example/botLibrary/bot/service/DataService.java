package org.example.botLibrary.bot.service;

import org.example.botLibrary.bot.pojo.CurrentState;

public interface DataService {
    void put(String key, Object value);
    Object get(String key);
    void remove(String key);

    CurrentState getCurrentState(String key);
    void updateCurrentState(String key, CurrentState currentState);
    void removeCurrentState(String key);
}
