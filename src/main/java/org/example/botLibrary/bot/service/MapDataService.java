package org.example.botLibrary.bot.service;

import java.util.HashMap;
import java.util.Map;

public class MapDataService implements DataService {

    private static MapDataService instance;
    private final Map<String, Object> dataMap;

    private MapDataService() {
        dataMap = new HashMap<>();
    }

    public static MapDataService getInstance() {
        if (instance == null) {
            instance = new MapDataService();
        }
        return instance;
    }

    @Override
    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    @Override
    public Object get(String key) {
        return dataMap.get(key);
    }

    @Override
    public void remove(String key) {
        dataMap.remove(key);
    }
}
