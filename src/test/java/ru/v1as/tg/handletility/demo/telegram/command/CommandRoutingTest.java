package ru.v1as.tg.handletility.demo.telegram.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.operation.handler.Handled;
import ru.v1as.tg.handletility.demo.NotesBot;
import ru.v1as.tg.handletility.demo.telegram.LogCapture;
import ru.v1as.tg.handletility.demo.telegram.RecordingTgSender;
import ru.v1as.tg.handletility.demo.telegram.TelegramUpdates;

class CommandRoutingTest {

    private final RecordingTgSender sender = new RecordingTgSender();
    private final NotesBot bot = new NotesBot(sender);

    @Test
    void routesKnownCommands() {
        bot.handle(TelegramUpdates.command(10L, 7L, "/notes"));
        bot.handle(TelegramUpdates.command(10L, 7L, "/note Title"));
        bot.handle(TelegramUpdates.command(10L, 7L, "/edit 1 text"));

        assertEquals(3, sender.executed().size());
    }

    @Test
    void unknownCommandWarnsWithoutReply() {
        try (LogCapture logs = new LogCapture(UnknownCommandHandler.class)) {
            Handled handled = bot.handle(TelegramUpdates.command(10L, 7L, "/foo"));

            assertTrue(handled.isDone());
            assertEquals(0, sender.executed().size());
            assertTrue(logs.warningContains("/foo"));
        }
    }
}
