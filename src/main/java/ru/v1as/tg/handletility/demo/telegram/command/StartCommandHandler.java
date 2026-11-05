package ru.v1as.tg.handletility.demo.telegram.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

public final class StartCommandHandler extends AbstractCommandHandler {

    static final String TEXT =
            """
            Демо handletility: заметки через команды и кнопки.

            /notes — список заметок
            /note <заголовок> <текст> — новая заметка
            /edit <id> <новый текст> — изменить текст
            """;

    private final TgSender sender;

    public StartCommandHandler(TgSender sender) {
        super("start");
        this.sender = sender;
    }

    @Override
    protected void onCommand(Command command) {
        sender.execute(SendMessage.builder().chatId(command.chatIdStr()).text(TEXT).build());
    }
}
