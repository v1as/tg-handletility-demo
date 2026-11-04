package ru.v1as.tg.handletility.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

class CommandTest {

    @Test
    void readsNameBotAndArguments() {
        Command command = Command.parse(message("/note@demo_bot Title hello"));

        assertEquals("note", command.name());
        assertEquals("demo_bot", command.botName());
        assertEquals(List.of("Title", "hello"), command.arguments());
        assertEquals(10L, command.chatId());
        assertEquals(7L, command.userId());
    }

    @Test
    void rejectsBrokenCommandText() {
        assertThrows(IllegalArgumentException.class, () -> Command.parse(message("/")));
    }

    private static Message message(String text) {
        return Message.builder()
                .messageId(1)
                .date(1)
                .chat(Chat.builder().id(10L).type("private").build())
                .from(User.builder().id(7L).isBot(false).firstName("u").build())
                .text(text)
                .build();
    }
}
