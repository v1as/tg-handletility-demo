package ru.v1as.tg.handletility.demo.telegram;

import java.util.List;
import ru.v1as.tg.handletility.demo.note.Note;

public final class NotesListFormat {

    private NotesListFormat() {}

    public static String format(List<Note> notes) {
        if (notes.isEmpty()) {
            return "Заметок пока нет. Создайте: /note <заголовок> <текст>";
        }
        StringBuilder text = new StringBuilder("Ваши заметки:\n");
        for (Note note : notes) {
            text.append('#').append(note.id()).append(" — ").append(note.title()).append('\n');
        }
        return text.toString().trim();
    }
}
