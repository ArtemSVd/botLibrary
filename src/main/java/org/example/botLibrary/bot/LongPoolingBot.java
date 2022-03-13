package org.example.botLibrary.bot;

import lombok.RequiredArgsConstructor;
import org.example.botLibrary.bot.pojo.HandleUpdateParams;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

@RequiredArgsConstructor
abstract class LongPoolingBot extends TelegramLongPollingBot {

    private String botUsername;
    private String botToken;

    @Override
    public abstract String getBotUsername();

    @Override
    public abstract String getBotToken();

    @Override
    public void onUpdateReceived(Update update) {
        this.onUpdateReceived(this.getParams(update));
    }

    public abstract void onUpdateReceived(HandleUpdateParams params);

    private HandleUpdateParams getParams(Update update) {
        Long chatId = null;
        Integer messageId = null;
        String command = null;
        String[] args = null;

        if (update.hasMessage()) { // Если сообщение пришло в ЛС боту
            chatId = update.getMessage().getChatId();
            command = this.getCommand(update.getMessage().getText());
            args = this.getArgs(update.getMessage().getText());
        } else if (update.hasCallbackQuery()) { // Если это коллбэк с кнопок
            chatId = update.getCallbackQuery().getMessage().getChatId();
            command = this.getCommand(update.getCallbackQuery().getData());
            args = this.getArgs(update.getCallbackQuery().getData());
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }

        return HandleUpdateParams.builder()
                .command(command)
                .arguments(args)
                .chatId(chatId)
                .isCallback(update.hasCallbackQuery())
                .messageId(messageId)
                .build();
    }

    private String getCommand(String message) {
        String[] strings = message.split(" ");
        return strings.length > 0 && strings[0].startsWith("/") ? strings[0] : null;
    }

    private String[] getArgs(String message) {
        String[] strings = message.split(" ");
        return Arrays.stream(strings)
                .filter(str -> !str.startsWith("/"))
                .map(String::trim)
                .toArray(String[]::new);
    }

}
