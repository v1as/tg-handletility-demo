package ru.v1as.tg.handletility.demo.telegram.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.handletility.demo.NotesBot;
import ru.v1as.tg.handletility.demo.telegram.RecordingTgSender;
import ru.v1as.tg.handletility.demo.telegram.TelegramUpdates;

class NotesCommandsTest {

    private final RecordingTgSender sender = new RecordingTgSender();
    private final NotesBot bot = new NotesBot(sender);

    @Test
    void createsNoteAndShowsItInTheList() {
        bot.handle(TelegramUpdates.command(10L, 7L, "/note Title hello world"));
        bot.handle(TelegramUpdates.command(10L, 7L, "/notes"));

        assertEquals(2, sender.executed().size());
        SendMessage created = (SendMessage) sender.executed().get(0);
        SendMessage list = (SendMessage) sender.executed().get(1);
        assertTrue(created.getText().contains("#1"));
        assertTrue(created.getText().contains("Title"));
        assertTrue(list.getText().contains("#1"));
        assertTrue(list.getText().contains("Title"));
    }

    @Test
    void invalidEditIdRepliesInsteadOfFailing() {
        bot.handle(TelegramUpdates.command(10L, 7L, "/edit nope text"));

        assertEquals(1, sender.executed().size());
        assertTrue(((SendMessage) sender.executed().getFirst()).getText().contains("nope"));
    }
}
