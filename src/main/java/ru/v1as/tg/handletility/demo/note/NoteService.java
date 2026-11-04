package ru.v1as.tg.handletility.demo.note;

import java.util.List;
import java.util.Optional;

public interface NoteService {

    Note create(long chatId, String title, String text);

    List<Note> list(long chatId);

    Optional<Note> find(long chatId, long noteId);

    Optional<Note> updateText(long chatId, long noteId, String text);

    boolean delete(long chatId, long noteId);
}
