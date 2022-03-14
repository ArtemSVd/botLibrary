package org.example.botLibrary.bot.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public class Script<T extends Enum<T>> {
    private Function<String, Enum<T>> currentStateFunc;
    private List<StateAction> stages;

    @Nullable
    public StateAction findActualStage(UpdateParams params) {
        Enum<T> enumObj = currentStateFunc != null ? currentStateFunc.apply(params.getChatId()) : null;
        return stages.stream()
                .filter(stage -> stage.getState().equals(enumObj))
                .findFirst().orElse(null);
    }
}
