package org.example.botLibrary.bot.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentState {
    private String lastCommand;
    private Enum<?> state;
    private boolean isFinalState = true;
}
