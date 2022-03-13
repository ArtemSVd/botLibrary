package org.example.botLibrary.bot.pojo;

import lombok.Getter;

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

    public Function<UpdateParams, Object> getActionByCommandName(String name, UpdateParams updateParams) {
        Command command = commands.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Неизвестная команда!"));
        if (command.getScript() != null) {
            Function<UpdateParams, Object> action = command.getScript().findActualAction(updateParams);
            return action != null ? action : command.getAction();
        } else {
            return command.getAction();
        }
    }
}
