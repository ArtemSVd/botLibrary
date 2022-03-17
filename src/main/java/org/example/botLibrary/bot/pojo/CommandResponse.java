package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommandResponse {
    private Enum<?> nextState;
    private Object message;
}
