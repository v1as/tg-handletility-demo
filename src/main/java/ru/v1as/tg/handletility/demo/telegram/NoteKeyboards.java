package ru.v1as.tg.handletility.demo.telegram;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.v1as.tg.handletility.demo.model.CallbackData;
import ru.v1as.tg.handletility.demo.note.Note;

public final class NoteKeyboards {

    private NoteKeyboards() {}

    public static InlineKeyboardMarkup list(List<Note> notes) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (Note note : notes) {
            rows.add(new InlineKeyboardRow(button(note.title(), CallbackData.open(note.id()))));
        }
        rows.add(new InlineKeyboardRow(button("Обновить", CallbackData.list())));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public static InlineKeyboardMarkup note(long noteId) {
        return InlineKeyboardMarkup.builder()
                .keyboard(
                        List.of(
                                new InlineKeyboardRow(
                                        button("Удалить", CallbackData.delete(noteId)),
                                        button("К списку", CallbackData.list()))))
                .build();
    }

    private static InlineKeyboardButton button(String text, String data) {
        return InlineKeyboardButton.builder().text(text).callbackData(data).build();
    }
}
