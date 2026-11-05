package ru.v1as.tg.handletility.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

class CallbackTest {

    @Test
    void copiesQueryFields() {
        Callback callback = Callback.from(query("n|o|5", "cb-1"));

        assertEquals("n|o|5", callback.data());
        assertEquals(10L, callback.chatId());
        assertEquals(4, callback.messageId());
        assertEquals("cb-1", callback.callbackQueryId());
    }

    @Test
    void keepsMissingDataAsEmptyString() {
        Callback callback = Callback.from(query(null, "cb-2"));

        assertEquals("", callback.data());
        assertEquals("cb-2", callback.callbackQueryId());
    }

    private static CallbackQuery query(String data, String id) {
        User user = User.builder().id(7L).isBot(false).firstName("u").build();
        Message message =
                Message.builder()
                        .messageId(4)
                        .date(1)
                        .chat(Chat.builder().id(10L).type("private").build())
                        .from(user)
                        .build();
        CallbackQuery query = new CallbackQuery();
        query.setId(id);
        query.setFrom(user);
        query.setData(data);
        query.setMessage(message);
        return query;
    }
}
