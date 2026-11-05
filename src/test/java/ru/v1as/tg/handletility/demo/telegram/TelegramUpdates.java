package ru.v1as.tg.handletility.demo.telegram;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public final class TelegramUpdates {

    private TelegramUpdates() {}

    public static Update command(long chatId, long userId, String text) {
        int commandLength = text.indexOf(' ') < 0 ? text.length() : text.indexOf(' ');
        MessageEntity entity =
                MessageEntity.builder()
                        .type(EntityType.BOTCOMMAND)
                        .offset(0)
                        .length(commandLength)
                        .build();
        return messageUpdate(chatId, userId, text, List.of(entity));
    }

    public static Update plainText(long chatId, long userId, String text) {
        return messageUpdate(chatId, userId, text, null);
    }

    private static Update messageUpdate(
            long chatId, long userId, String text, List<MessageEntity> entities) {
        User user = User.builder().id(userId).isBot(false).firstName("u").build();
        Chat chat = Chat.builder().id(chatId).type("private").build();
        Message message =
                Message.builder()
                        .messageId(1)
                        .date(1)
                        .chat(chat)
                        .from(user)
                        .text(text)
                        .entities(entities)
                        .build();
        Update update = new Update();
        update.setUpdateId(1);
        update.setMessage(message);
        return update;
    }

    public static Update callback(
            long chatId, long userId, int messageId, String data, String callbackId) {
        User user = User.builder().id(userId).isBot(false).firstName("u").build();
        Chat chat = Chat.builder().id(chatId).type("private").build();
        Message message =
                Message.builder().messageId(messageId).date(1).chat(chat).from(user).build();
        CallbackQuery query = new CallbackQuery();
        query.setId(callbackId);
        query.setFrom(user);
        query.setData(data);
        query.setMessage(message);
        Update update = new Update();
        update.setUpdateId(2);
        update.setCallbackQuery(query);
        return update;
    }
}
