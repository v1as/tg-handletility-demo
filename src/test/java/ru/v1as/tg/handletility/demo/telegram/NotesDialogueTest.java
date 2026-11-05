package ru.v1as.tg.handletility.demo.telegram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.v1as.tg.handletility.demo.NotesBot;
import ru.v1as.tg.handletility.demo.model.CallbackData;
import ru.v1as.tg.handletility.demo.telegram.callback.UnknownCallbackHandler;
import ru.v1as.tg.handletility.demo.telegram.command.UnknownCommandHandler;

class NotesDialogueTest {

    @Test
    void playsTheWholeDialogueWithoutTelegram() {
        RecordingTgSender sender = new RecordingTgSender();
        NotesBot bot = new NotesBot(sender);

        try (LogCapture commands = new LogCapture(UnknownCommandHandler.class);
                LogCapture callbacks = new LogCapture(UnknownCallbackHandler.class)) {
            bot.handle(TelegramUpdates.command(10L, 7L, "/note Title hello world"));
            bot.handle(TelegramUpdates.callback(10L, 7L, 4, CallbackData.open(1), "cb-open"));
            bot.handle(TelegramUpdates.callback(10L, 7L, 4, CallbackData.delete(1), "cb-delete"));
            bot.handle(TelegramUpdates.command(10L, 7L, "/foo"));
            bot.handle(TelegramUpdates.callback(10L, 7L, 4, "nope", "cb-nope"));

            assertEquals(6, sender.executed().size());

            SendMessage created = assertInstanceOf(SendMessage.class, sender.executed().get(0));
            assertTrue(created.getText().contains("Title"));

            assertEquals("cb-open", callbackId(sender.executed().get(1)));
            EditMessageText opened =
                    assertInstanceOf(EditMessageText.class, sender.executed().get(2));
            assertEquals(4, opened.getMessageId());
            assertTrue(opened.getText().contains("hello world"));

            assertEquals("cb-delete", callbackId(sender.executed().get(3)));
            EditMessageText deleted =
                    assertInstanceOf(EditMessageText.class, sender.executed().get(4));
            assertTrue(deleted.getText().contains("удалена"));
            assertTrue(deleted.getText().contains("Заметок пока нет"));
            assertFalse(deleted.getText().contains("hello world"));

            assertEquals("cb-nope", callbackId(sender.executed().get(5)));
            assertTrue(commands.warningContains("/foo"));
            assertTrue(callbacks.warningContains("nope"));
        }
    }

    private static String callbackId(BotApiMethod<?> method) {
        return assertInstanceOf(AnswerCallbackQuery.class, method).getCallbackQueryId();
    }
}
