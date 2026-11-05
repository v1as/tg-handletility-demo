package ru.v1as.tg.handletility.demo.telegram.callback;

import java.util.List;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.v1as.tg.handletility.demo.model.CallbackAction;
import ru.v1as.tg.handletility.demo.model.NoteCallback;
import ru.v1as.tg.handletility.demo.note.Note;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.NoteKeyboards;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

public final class DeleteNoteCallbackHandler extends AbstractNoteCallbackHandler {

    private final TgSender sender;
    private final NoteService notes;

    public DeleteNoteCallbackHandler(TgSender sender, NoteService notes) {
        super(CallbackAction.DELETE);
        this.sender = sender;
        this.notes = notes;
    }

    @Override
    protected void onCallback(NoteCallback callback) {
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
                        .text(head + "\n\n" + notesListText(chatNotes))
                        .replyMarkup(NoteKeyboards.list(chatNotes))
                        .build());
    }
}
