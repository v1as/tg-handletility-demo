package ru.v1as.tg.handletility.demo.telegram;

import java.io.Serializable;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class OkHttpTgSender implements TgSender {

    private final OkHttpTelegramClient client;

    public OkHttpTgSender(String token) {
        this.client = new OkHttpTelegramClient(token);
    }

    @Override
    public <T extends Serializable, M extends BotApiMethod<T>> T execute(M method) {
        try {
            return client.execute(method);
        } catch (TelegramApiException e) {
            throw new IllegalStateException(e);
        }
    }
}
