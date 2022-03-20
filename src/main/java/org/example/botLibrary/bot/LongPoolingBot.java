package org.example.botLibrary.bot;

import lombok.RequiredArgsConstructor;
import org.example.botLibrary.bot.pojo.UpdateParams;
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

    public abstract void onUpdateReceived(UpdateParams params);

    private UpdateParams getParams(Update update) {
        Long chatId = null;
        Integer messageId = null;
        String command = null;
        String[] args = null;
        String fileId = null;

        if (update.hasMessage()) { // Если сообщение пришло в ЛС боту
            chatId = update.getMessage().getChatId();
            command = this.getCommand(update.getMessage().getText());
            args = this.getArgs(update.getMessage().getText());
            if (update.getMessage().getDocument() != null) {
                fileId = update.getMessage().getDocument().getFileId();
            }
        } else if (update.hasCallbackQuery()) { // Если это коллбэк с кнопок
            chatId = update.getCallbackQuery().getMessage().getChatId();
            command = this.getCommand(update.getCallbackQuery().getData());
            args = this.getArgs(update.getCallbackQuery().getData());
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }

        return UpdateParams.builder()
                .command(command)
                .arguments(args)
                .chatId(String.valueOf(chatId))
                .isCallback(update.hasCallbackQuery())
                .messageId(messageId)
                .fileId(fileId)
                .botName(this.getBotUsername())
                .botToken(this.getBotToken())
                .build();
    }

    private String getCommand(String message) {
        if (message == null) {
            return null;
        }
        String[] strings = message.split(" ");
        return strings.length > 0 && strings[0].startsWith("/") ? strings[0] : null;
    }

    private String[] getArgs(String message) {
        if (message == null) {
            return null;
        }
        String[] strings = message.split(" ");
        return Arrays.stream(strings)
                .filter(str -> !str.startsWith("/"))
                .map(String::trim)
                .toArray(String[]::new);
    }

}
