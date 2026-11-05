package ru.v1as.tg.handletility.demo.model;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public record UpdateContext(Update update, long chatId, long userId) {

    public boolean isCommand() {
        return update.hasMessage() && update.getMessage().isCommand();
    }

    public Message message() {
        return update.getMessage();
    }

    public boolean isCallback() {
        return update.hasCallbackQuery();
    }

    public CallbackQuery callbackQuery() {
        return update.getCallbackQuery();
    }
}
