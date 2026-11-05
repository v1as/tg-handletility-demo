package ru.v1as.tg.handletility.demo.telegram.callback;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.v1as.tg.handletility.demo.model.NoteCallback;
import ru.v1as.tg.handletility.demo.note.Note;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.NoteKeyboards;
import ru.v1as.tg.handletility.demo.telegram.NotesListFormat;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

@RequiredArgsConstructor
public final class DeleteNoteCallbackHandler implements NoteCallbackHandler {

    private final TgSender sender;
    private final NoteService notes;

    @Override
    public void handle(NoteCallback callback) {
        long noteId = callback.noteId();
        boolean deleted = notes.delete(callback.chatId(), noteId);
        String head =
                deleted
                        ? "Заметка #%d удалена.".formatted(noteId)
                        : "Заметка #%d не найдена.".formatted(noteId);
        List<Note> chatNotes = notes.list(callback.chatId());
        sender.execute(
                EditMessageText.builder()
                        .chatId(callback.chatIdStr())
                        .messageId(callback.messageId())
                        .text(head + "\n\n" + NotesListFormat.format(chatNotes))
                        .replyMarkup(NoteKeyboards.list(chatNotes))
                        .build());
    }
}
