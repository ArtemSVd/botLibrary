package org.example.botLibrary.bot.pojo;

import lombok.Getter;
import org.example.botLibrary.bot.ScriptBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
public class BotConfiguration {
    private List<Command> commands;
    private Script<?> nonCommandScript;

    public void addCommand(@NotNull Command command) {
        if (commands == null) {
            commands = new ArrayList<>();
        }
        commands.add(command);
    }

    public void addNonCommandScript(@NotNull ScriptBuilder<?> scriptBuilder) {
        scriptBuilder.start();
        this.nonCommandScript = scriptBuilder.getScript();
    }

    public CommandResponse applyNonCommandAction(UpdateParams updateParams) {
        StateAction actualStage = nonCommandScript.findActualStage(updateParams);
        Function<UpdateParams, CommandResponse> action = actualStage != null ? actualStage.getAction() : null;
        return action != null ? action.apply(updateParams) : CommandResponse.builder().build();
    }

    public CommandResponse applyAction(String commandName, UpdateParams updateParams) {
        Command command = commands.stream()
                .filter(c -> c.getName().equals(commandName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Неизвестная команда!"));

        Function<UpdateParams, CommandResponse> action = command.getAction();
        if (command.getScript() != null) {
            StateAction actualStage = command.getScript().findActualStage(updateParams);
            if (actualStage != null) {
                action = actualStage.getAction();
            }
        }
        return action.apply(updateParams);
    }
}
