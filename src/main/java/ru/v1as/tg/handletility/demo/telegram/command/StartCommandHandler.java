package ru.v1as.tg.handletility.demo.telegram.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

@RequiredArgsConstructor
public final class StartCommandHandler implements CommandHandler {

    @Getter private final String name = "start";

    static final String TEXT =
            """
            Демо handletility: заметки через команды и кнопки.

            /notes — список заметок
            /note <заголовок> <текст> — новая заметка
            /edit <id> <новый текст> — изменить текст
            """;

    private final TgSender sender;

    @Override
    public void onCommand(Command command) {
        sender.execute(SendMessage.builder().chatId(command.chatIdStr()).text(TEXT).build());
    }
}
