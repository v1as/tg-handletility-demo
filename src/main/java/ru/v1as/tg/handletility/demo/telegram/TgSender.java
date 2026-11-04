package ru.v1as.tg.handletility.demo.telegram;

import java.io.Serializable;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

public interface TgSender {

    <T extends Serializable, M extends BotApiMethod<T>> T execute(M method);
}
