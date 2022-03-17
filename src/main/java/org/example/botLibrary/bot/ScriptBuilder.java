package org.example.botLibrary.bot;

import lombok.Getter;
import org.example.botLibrary.bot.pojo.*;

import java.util.ArrayList;
import java.util.function.Function;

@Getter
public abstract class ScriptBuilder<T extends Enum<T>> {
    private Script<T> script;

    public void start() {
        script = new Script<>();
        initialize();
        script.setCurrentStateFunc(this.getCurrentState());
        script.setStartState(this.getStartState());
    }

    public abstract void initialize();
    public abstract Enum<T> getStartState();

    protected Function<String, Enum<T>> getCurrentState() {
        return key -> {
            CurrentState currentState = TelegramBot.dataService.getCurrentState(key);
            return currentState != null && currentState.getState() != null ?
                    (Enum<T>) currentState.getState() : script.getStartState();
        };
    }

    public void addStage(T state, Function<UpdateParams, CommandResponse> action) {
        if (script.getStages() == null) {
            script.setStages(new ArrayList<>());
        }
        script.getStages().add(StateAction.builder()
                .state(state)
                .action(action)
                .build());
    }
}
