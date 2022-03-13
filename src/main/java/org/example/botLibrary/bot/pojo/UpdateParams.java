package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateParams {
    private final String chatId;
    private final String[] arguments;
    private final boolean isCallback;
    private final Integer messageId;
}
