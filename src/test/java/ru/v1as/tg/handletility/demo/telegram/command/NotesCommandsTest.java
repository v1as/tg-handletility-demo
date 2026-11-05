package ru.v1as.tg.handletility.demo.telegram.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

        InlineKeyboardMarkup createdKeyboard = (InlineKeyboardMarkup) created.getReplyMarkup();
        assertEquals("n|d|1", createdKeyboard.getKeyboard().get(0).get(0).getCallbackData());
        assertEquals("n|l", createdKeyboard.getKeyboard().get(0).get(1).getCallbackData());

        InlineKeyboardMarkup listKeyboard = (InlineKeyboardMarkup) list.getReplyMarkup();
        InlineKeyboardButton open = listKeyboard.getKeyboard().get(0).get(0);
        assertEquals("Title", open.getText());
        assertEquals("n|o|1", open.getCallbackData());
        assertEquals("n|l", listKeyboard.getKeyboard().get(1).get(0).getCallbackData());
    }

    @Test
    void invalidEditIdRepliesInsteadOfFailing() {
        bot.handle(TelegramUpdates.command(10L, 7L, "/edit nope text"));

        assertEquals(1, sender.executed().size());
        assertTrue(((SendMessage) sender.executed().getFirst()).getText().contains("nope"));
    }
}
