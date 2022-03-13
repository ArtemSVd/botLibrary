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

    public ScriptBuilder() {
        script = new Script<>();
        initialize();
        script.setCurrentStateFunc(this.getCurrentState());
    }

    public abstract void initialize();
    public abstract Function<String, Enum<T>> getCurrentState();

    public void addStage(T enumObj, Function<UpdateParams, Object> action) {
        if (script.getStages() == null) {
            script.setStages(new ArrayList<>());
        }
        script.getStages().add(StateAction.builder()
                .state(enumObj)
                .action(action)
                .build());
    }
}
