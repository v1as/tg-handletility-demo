package ru.v1as.tg.handletility.demo.model;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public final class UpdateContexts {

    private UpdateContexts() {}

    public static UpdateContext parse(Update update) {
        if (update == null || !update.hasMessage()) {
            return null;
        }
        Message message = update.getMessage();
        User user = message.getFrom();
        if (message.getChat() == null || user == null) {
            return null;
        }
        return new UpdateContext(update, message.getChatId(), user.getId());
    }
}
