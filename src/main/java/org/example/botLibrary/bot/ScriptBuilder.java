package org.example.botLibrary.bot;

import lombok.Getter;
import org.example.botLibrary.bot.pojo.Script;
import org.example.botLibrary.bot.pojo.StateAction;
import org.example.botLibrary.bot.pojo.UpdateParams;

import java.util.ArrayList;
import java.util.function.Function;

@Getter
public abstract class ScriptBuilder<T extends Enum<T>> {
    private Script<T> script;

    public void start() {
        script = new Script<>();
        initialize();
        script.setCurrentStateFunc(this.getCurrentState());
    }

    public abstract void initialize();
    public abstract Function<String, Enum<T>> getCurrentState();

    public void addStage(T state, T nextState, Function<UpdateParams, Object> action) {
        addStage(state, nextState, action, false);
    }

    public void addStage(T state, T nextState, Function<UpdateParams, Object> action, boolean isFinal) {
        if (script.getStages() == null) {
            script.setStages(new ArrayList<>());
        }
        script.getStages().add(StateAction.builder()
                .state(state)
                .nextState(nextState)
                .action(action)
                .isFinal(isFinal)
                .build());
    }
}
