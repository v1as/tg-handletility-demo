package ru.v1as.tg.handletility.demo.telegram.command;

import java.util.List;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.note.Note;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.NoteKeyboards;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

public final class CreateNoteCommandHandler extends AbstractCommandHandler {

    private final TgSender sender;
    private final NoteService notes;

    public CreateNoteCommandHandler(TgSender sender, NoteService notes) {
        super("note");
        this.sender = sender;
        this.notes = notes;
    }

    @Override
    protected void onCommand(Command command) {
        List<String> arguments = command.arguments();
        if (arguments.size() < 2) {
            sender.execute(
                    SendMessage.builder()
                            .chatId(command.chatIdStr())
                            .text("Использование: /note <заголовок> <текст>")
                            .build());
            return;
        }
        String title = arguments.get(0);
        String body = String.join(" ", arguments.subList(1, arguments.size()));
        Note note = notes.create(command.chatId(), title, body);
        sender.execute(
                SendMessage.builder()
                        .chatId(command.chatIdStr())
                        .text("Создана заметка #%d: %s".formatted(note.id(), note.title()))
                        .replyMarkup(NoteKeyboards.note(note.id()))
                        .build());
    }
}
