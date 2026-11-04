package ru.v1as.tg.handletility.demo.telegram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.message.Message;

class RecordingTgSenderTest {

    private final RecordingTgSender sender = new RecordingTgSender();

    @Test
    void recordsOutgoingCallsAndSuppliesMessageIds() {
        Message sent = sender.execute(SendMessage.builder().chatId("10").text("hello").build());
        Serializable edited =
                sender.execute(
                        EditMessageText.builder()
                                .chatId("10")
                                .messageId(sent.getMessageId())
                                .text("edited")
                                .build());
        Boolean answered =
                sender.execute(AnswerCallbackQuery.builder().callbackQueryId("cb").build());

        assertEquals(1, sent.getMessageId());
        assertEquals(Boolean.TRUE, edited);
        assertTrue(answered);
        assertEquals(3, sender.executed().size());
        assertInstanceOf(SendMessage.class, sender.executed().get(0));
        assertEquals("hello", ((SendMessage) sender.executed().get(0)).getText());
        assertInstanceOf(EditMessageText.class, sender.executed().get(1));
        assertInstanceOf(AnswerCallbackQuery.class, sender.executed().get(2));
    }
}
