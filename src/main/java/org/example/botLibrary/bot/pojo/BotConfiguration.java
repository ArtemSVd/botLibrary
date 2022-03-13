package org.example.botLibrary.bot.pojo;

import lombok.Getter;
import org.example.botLibrary.bot.TelegramBot;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
public class BotConfiguration {
    private List<Command> commands;

    public void addCommand(@NotNull Command command) {
        if (commands == null) {
            commands = new ArrayList<>();
        }
        commands.add(command);
    }

    public Object applyAction(String commandName, UpdateParams updateParams) {
        Command command = commands.stream()
                .filter(c -> c.getName().equals(commandName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Неизвестная команда!"));

        boolean isFinalState = true;
        Enum<?> state = null;
        Function<UpdateParams, Object> action = command.getAction();
        if (command.getScript() != null) {
            StateAction actualStage = command.getScript().findActualStage(updateParams);
            if (actualStage != null) {
                action = actualStage.getAction();
                isFinalState = actualStage.isFinal();
                state = actualStage.getNextState();
            }
        }

        TelegramBot.dataService.setCurrentState(updateParams.getChatId(), CurrentState.builder()
                .lastCommand(commandName)
                .isFinalState(isFinalState)
                .state(state)
                .build());

        return action.apply(updateParams);
    }
}
