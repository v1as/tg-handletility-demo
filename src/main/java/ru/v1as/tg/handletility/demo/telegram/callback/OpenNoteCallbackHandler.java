package ru.v1as.tg.handletility.demo.telegram.callback;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.v1as.tg.handletility.demo.model.CallbackAction;
import ru.v1as.tg.handletility.demo.model.NoteCallback;
import ru.v1as.tg.handletility.demo.note.Note;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.NoteKeyboards;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

@RequiredArgsConstructor
public final class OpenNoteCallbackHandler implements NoteCallbackHandler {

    @Getter private final CallbackAction action = CallbackAction.OPEN;

    private final TgSender sender;
    private final NoteService notes;

    @Override
    public void onCallback(NoteCallback callback) {
        long noteId = callback.noteId();
        Optional<Note> note = notes.find(callback.chatId(), noteId);
        if (note.isEmpty()) {
            List<Note> chatNotes = notes.list(callback.chatId());
            sender.execute(
                    EditMessageText.builder()
                            .chatId(callback.chatIdStr())
                            .messageId(callback.messageId())
                            .text("Заметка #%d не найдена.".formatted(noteId))
                            .replyMarkup(NoteKeyboards.list(chatNotes))
                            .build());
            return;
        }
        Note found = note.get();
        sender.execute(
                EditMessageText.builder()
                        .chatId(callback.chatIdStr())
                        .messageId(callback.messageId())
                        .text("#%d %s\n\n%s".formatted(found.id(), found.title(), found.text()))
                        .replyMarkup(NoteKeyboards.note(found.id()))
                        .build());
    }
}
