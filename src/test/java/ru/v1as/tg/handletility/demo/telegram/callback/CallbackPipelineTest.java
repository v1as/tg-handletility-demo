package ru.v1as.tg.handletility.demo.telegram.callback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.operation.handler.impl.switching.SwitchHandler;
import ru.v1as.tg.handletility.demo.NotesBot;
import ru.v1as.tg.handletility.demo.model.CallbackData;
import ru.v1as.tg.handletility.demo.telegram.LogCapture;
import ru.v1as.tg.handletility.demo.telegram.RecordingTgSender;
import ru.v1as.tg.handletility.demo.telegram.TelegramUpdates;

class CallbackPipelineTest {

    private final RecordingTgSender sender = new RecordingTgSender();
    private final NotesBot bot = new NotesBot(sender);

    @Test
    void openAnswersCallbackAndEditsTheMessage() {
        bot.handle(TelegramUpdates.command(10L, 7L, "/note Title hello"));
        bot.handle(TelegramUpdates.callback(10L, 7L, 4, CallbackData.open(1), "cb-open"));

        assertEquals(3, sender.executed().size());
        AnswerCallbackQuery answer =
                assertInstanceOf(AnswerCallbackQuery.class, sender.executed().get(1));
        EditMessageText edit = assertInstanceOf(EditMessageText.class, sender.executed().get(2));
        assertEquals("cb-open", answer.getCallbackQueryId());
        assertEquals(4, edit.getMessageId());
        assertTrue(edit.getText().contains("hello"));
    }

    @Test
    void unknownCallbackAnswersAndWarnsWithoutEditing() {
        try (LogCapture logs = new LogCapture(UnknownCallbackHandler.class)) {
            bot.handle(TelegramUpdates.callback(10L, 7L, 4, "weird", "cb-x"));

            assertEquals(1, sender.executed().size());
            assertInstanceOf(AnswerCallbackQuery.class, sender.executed().getFirst());
            assertTrue(logs.warningContains("weird"));
            assertTrue(logs.warningHasMdc("chatId", "10"));
            assertTrue(logs.warningHasMdc("userId", "7"));
        }
    }

    @Test
    void logsRoutedCallbackAtInfo() {
        try (LogCapture logs = new LogCapture(SwitchHandler.class, "CallbackHandler")) {
            bot.handle(TelegramUpdates.callback(10L, 7L, 4, CallbackData.open(1), "cb-open"));

            assertTrue(logs.infoContains("OPEN"));
            assertTrue(logs.infoHasMdc("chatId", "10"));
            assertTrue(logs.infoHasMdc("userId", "7"));
        }
    }
}
