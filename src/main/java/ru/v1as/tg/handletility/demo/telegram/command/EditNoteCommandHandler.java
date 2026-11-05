package ru.v1as.tg.handletility.demo.telegram.command;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.note.Note;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

@RequiredArgsConstructor
public final class EditNoteCommandHandler implements CommandHandler {

    @Getter private final String name = "edit";

    private final TgSender sender;
    private final NoteService notes;

    @Override
    public void onCommand(Command command) {
        List<String> arguments = command.arguments();
        if (arguments.size() < 2) {
            sender.execute(
                    SendMessage.builder()
                            .chatId(command.chatIdStr())
                            .text("Использование: /edit <id> <новый текст>")
                            .build());
            return;
        }
        long noteId;
        try {
            noteId = Long.parseLong(arguments.get(0));
        } catch (NumberFormatException exception) {
            String raw = arguments.get(0);
            sender.execute(
                    SendMessage.builder()
                            .chatId(command.chatIdStr())
                            .text("Некорректный id заметки: %s".formatted(raw))
                            .build());
            return;
        }
        String text = String.join(" ", arguments.subList(1, arguments.size()));
        Optional<Note> updated = notes.updateText(command.chatId(), noteId, text);
        if (updated.isEmpty()) {
            sender.execute(
                    SendMessage.builder()
                            .chatId(command.chatIdStr())
                            .text("Заметка #%d не найдена.".formatted(noteId))
                            .build());
            return;
        }
        Note note = updated.get();
        String view = "#%d %s\n\n%s".formatted(note.id(), note.title(), note.text());
        sender.execute(
                SendMessage.builder()
                        .chatId(command.chatIdStr())
                        .text("Заметка #%d обновлена.\n%s".formatted(note.id(), view))
                        .build());
    }
}
