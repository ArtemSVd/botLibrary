package org.example.botLibrary.bot.service;

import org.example.botLibrary.bot.pojo.CurrentState;

public interface DataService {
    void put(String key, Object value);
    Object get(String key);
    void remove(String key);

    CurrentState getCurrentState(String key);
    void updateLastCommand(String key, String lastCommand);
    void updateState(String key, Enum<?> state);
    void removeCurrentState(String key);
}
