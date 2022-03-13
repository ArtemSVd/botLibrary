package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;
import org.example.botLibrary.bot.ScriptBuilder;

import java.util.function.Function;

@Builder
@Getter
public class Command {
    private String name;
    private Function<UpdateParams, Object> action;
    private Script<?> script;

    public Command setScript(ScriptBuilder<?> scriptBuilder) {
        this.script = scriptBuilder.getScript();
        return this;
    }
}
