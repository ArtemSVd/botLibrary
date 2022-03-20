package org.example.botLibrary.bot.util;

import org.example.botLibrary.bot.pojo.UpdateParams;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class TelegramUtil {
    public static SendMessage sendMessage(String chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    public static SendMessage sendMessage(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public static EditMessageText editMessage(UpdateParams params, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return EditMessageText.builder()
                .chatId(params.getChatId())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .messageId(params.getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public static SendDocument sendDocument(String chatId, InputFile inputFile) {
        return SendDocument.builder()
                .chatId(chatId)
                .document(inputFile)
                .build();
    }
}
