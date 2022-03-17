package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;
import org.example.botLibrary.bot.ScriptBuilder;

import java.util.function.Function;

@Builder
@Getter
public class Command {
    private final String name;
    private final Function<UpdateParams, CommandResponse> action;
    private Script<?> script;

    public Command setScript(ScriptBuilder<?> scriptBuilder) {
        scriptBuilder.start();
        this.script = scriptBuilder.getScript();
        return this;
    }
}
