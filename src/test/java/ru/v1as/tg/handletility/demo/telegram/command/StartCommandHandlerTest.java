package ru.v1as.tg.handletility.demo.telegram.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.operation.handler.Handled;
import ru.v1as.tg.handletility.demo.NotesBot;
import ru.v1as.tg.handletility.demo.telegram.RecordingTgSender;
import ru.v1as.tg.handletility.demo.telegram.TelegramUpdates;

class StartCommandHandlerTest {

    private final RecordingTgSender sender = new RecordingTgSender();
    private final NotesBot bot = new NotesBot(sender);

    @Test
    void startSendsHelp() {
        Handled handled = bot.handle(TelegramUpdates.command(10L, 7L, "/start"));

        assertTrue(handled.isDone());
        assertEquals(1, sender.executed().size());
        assertEquals(
                StartCommandHandler.TEXT, ((SendMessage) sender.executed().getFirst()).getText());
    }

    @Test
    void plainTextIsSkippedBeforeParse() {
        Handled handled = bot.handle(TelegramUpdates.plainText(10L, 7L, "hello"));

        assertTrue(handled.isSkipped());
        assertEquals(0, sender.executed().size());
    }

    @Test
    void brokenCommandIsAnErrorWithoutAReply() {
        Handled handled = bot.handle(TelegramUpdates.command(10L, 7L, "/"));

        assertTrue(handled.isError());
        assertEquals(0, sender.executed().size());
    }
}
