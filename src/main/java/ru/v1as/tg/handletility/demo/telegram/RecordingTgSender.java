package ru.v1as.tg.handletility.demo.telegram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public final class RecordingTgSender implements TgSender {

    private final List<BotApiMethod<?>> executed = new ArrayList<>();
    private int nextMessageId = 1;

    public List<BotApiMethod<?>> executed() {
        return List.copyOf(executed);
    }

    @Override
    public <T extends Serializable, M extends BotApiMethod<T>> T execute(M method) {
        executed.add(method);
        if (method instanceof SendMessage sendMessage) {
            Message message =
                    Message.builder()
                            .messageId(nextMessageId++)
                            .date(1)
                            .chat(
                                    Chat.builder()
                                            .id(Long.parseLong(sendMessage.getChatId()))
                                            .type("private")
                                            .build())
                            .text(sendMessage.getText())
                            .build();
            return cast(message);
        }
        if (method instanceof EditMessageText || method instanceof AnswerCallbackQuery) {
            return cast(Boolean.TRUE);
        }
        throw new IllegalArgumentException(
                "Unsupported Telegram method: " + method.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T cast(Serializable value) {
        return (T) value;
    }
}
