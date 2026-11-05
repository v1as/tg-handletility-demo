package ru.v1as.tg.handletility.demo.telegram.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.operation.handler.Handled;
import ru.operation.handler.impl.switching.SwitchHandler;
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
            assertTrue(logs.warningHasMdc("chatId", "10"));
            assertTrue(logs.warningHasMdc("userId", "7"));
        }
    }

    @Test
    void logsRoutedCommandAtInfo() {
        try (LogCapture logs = new LogCapture(SwitchHandler.class, "CommandHandler")) {
            bot.handle(TelegramUpdates.command(10L, 7L, "/notes"));

            assertTrue(logs.infoContains("notes"));
            assertTrue(logs.infoHasMdc("chatId", "10"));
            assertTrue(logs.infoHasMdc("userId", "7"));
        }
    }
}
