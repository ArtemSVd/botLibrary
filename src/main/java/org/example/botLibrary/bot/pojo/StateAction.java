package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
@Builder
public class StateAction {
    private Enum<?> state;
    private Enum<?> nextState;
    private Function<UpdateParams, Object> action;
    private boolean isFinal;
}
