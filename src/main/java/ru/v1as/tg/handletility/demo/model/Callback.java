package ru.v1as.tg.handletility.demo.model;

import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;

public record Callback(String data, long chatId, int messageId, String callbackQueryId) {

    public Callback {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(callbackQueryId, "callbackQueryId");
    }

    public String chatIdStr() {
        return Long.toString(chatId);
    }

    public static Callback from(CallbackQuery query) {
        String data = query.getData() == null ? "" : query.getData();
        MaybeInaccessibleMessage message = query.getMessage();
        return new Callback(data, message.getChatId(), message.getMessageId(), query.getId());
    }
}
