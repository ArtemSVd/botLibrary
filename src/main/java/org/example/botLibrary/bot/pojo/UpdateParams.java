package org.example.botLibrary.bot.pojo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateParams {
    private String chatId;
}
