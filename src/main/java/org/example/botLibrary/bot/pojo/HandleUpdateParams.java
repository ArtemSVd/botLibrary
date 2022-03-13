package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HandleUpdateParams {
    private String username;
    private Long chatId;
    private Integer messageId;
    private String command;
    private String[] arguments;
}
