package org.example.botLibrary.bot.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public class Script<T extends Enum<T>> {
    private Function<UpdateParams, Enum<T>> currentStateFunc;
    private Enum<T> startState;
    private List<StateAction> stages;

    @Nullable
    public StateAction findActualStage(UpdateParams params) {
        Enum<T> enumObj = currentStateFunc != null ? currentStateFunc.apply(params) : startState;
        return stages.stream()
                .filter(stage -> stage.getState().equals(enumObj))
                .findFirst().orElse(null);
    }
}
