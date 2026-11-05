package ru.v1as.tg.handletility.demo.telegram.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.NotesListFormat;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

@RequiredArgsConstructor
public final class ListNotesCommandHandler {

    private final TgSender sender;
    private final NoteService notes;

    public void handle(Command command) {
        var chatNotes = notes.list(command.chatId());
        sender.execute(
                SendMessage.builder()
                        .chatId(command.chatIdStr())
                        .text(NotesListFormat.format(chatNotes))
                        .build());
    }
}
