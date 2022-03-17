package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateParams {
    private String command;
    private String chatId;
    private String[] arguments;
    private boolean isCallback;
    private Integer messageId;

    private Object entity;
    private CurrentState currentState;
}
