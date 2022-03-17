package org.example.botLibrary.bot.service;

import org.example.botLibrary.bot.pojo.CurrentState;

import java.util.HashMap;
import java.util.Map;

public class MapDataService implements DataService {

    private static MapDataService instance;
    private final Map<String, Object> dataMap;

    private static final String CURRENT_DATA = "CURRENT_DATA_";

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

    @Override
    public void updateLastCommand(String key, String lastCommand) {
        CurrentState savedCurrentState = this.getCurrentState(key);
        savedCurrentState.setLastCommand(lastCommand);
        dataMap.put(CURRENT_DATA + key, savedCurrentState);
    }

    @Override
    public void updateState(String key, Enum<?> state) {
        CurrentState savedCurrentState = this.getCurrentState(key);
        savedCurrentState.setState(state);
        dataMap.put(CURRENT_DATA + key, savedCurrentState);
    }

    @Override
    public CurrentState getCurrentState(String key) {
        CurrentState currentState = (CurrentState) dataMap.get(CURRENT_DATA + key);
        if (currentState == null) {
            currentState = new CurrentState();
        }
        return currentState;
    }

    @Override
    public void removeCurrentState(String key) {
        dataMap.remove(CURRENT_DATA + key);
    }
}
