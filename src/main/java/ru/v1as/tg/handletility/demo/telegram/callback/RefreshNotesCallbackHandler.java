package ru.v1as.tg.handletility.demo.telegram.callback;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.v1as.tg.handletility.demo.model.CallbackAction;
import ru.v1as.tg.handletility.demo.model.NoteCallback;
import ru.v1as.tg.handletility.demo.note.Note;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.NoteKeyboards;
import ru.v1as.tg.handletility.demo.telegram.NotesListFormat;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

@RequiredArgsConstructor
public final class RefreshNotesCallbackHandler implements NoteCallbackHandler {

    @Getter private final CallbackAction action = CallbackAction.LIST;

    private final TgSender sender;
    private final NoteService notes;

    @Override
    public void onCallback(NoteCallback callback) {
        List<Note> chatNotes = notes.list(callback.chatId());
        sender.execute(
                EditMessageText.builder()
                        .chatId(callback.chatIdStr())
                        .messageId(callback.messageId())
                        .text(NotesListFormat.format(chatNotes))
                        .replyMarkup(NoteKeyboards.list(chatNotes))
                        .build());
    }
}
