package org.example.botLibrary.bot.service;

public interface DataService {
    void put(String key, Object value);
    Object get(String key);
    void remove(String key);
}
