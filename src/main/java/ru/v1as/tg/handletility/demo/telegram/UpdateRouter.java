package ru.v1as.tg.handletility.demo.telegram;

import static org.slf4j.event.Level.INFO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.operation.MdcEntry;
import ru.operation.handler.Handled;
import ru.operation.handler.Handler;
import ru.operation.handler.HandlerBuilder;
import ru.operation.handler.IdentifiedHandler;
import ru.operation.handler.impl.MdcHandler;
import ru.operation.handler.impl.switching.SwitchHandler;
import ru.operation.processor.Processor;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.model.UpdateContext;
import ru.v1as.tg.handletility.demo.model.UpdateContexts;
import ru.v1as.tg.handletility.demo.note.NoteService;
import ru.v1as.tg.handletility.demo.telegram.command.CreateNoteCommandHandler;
import ru.v1as.tg.handletility.demo.telegram.command.EditNoteCommandHandler;
import ru.v1as.tg.handletility.demo.telegram.command.ListNotesCommandHandler;
import ru.v1as.tg.handletility.demo.telegram.command.StartCommandHandler;
import ru.v1as.tg.handletility.demo.telegram.command.UnknownCommandHandler;

public final class UpdateRouter implements Handler<Update> {

    private final Handler<Update> routes;

    public UpdateRouter(TgSender sender, NoteService notes) {
        List<IdentifiedHandler<String, Command>> commands =
                Stream.<IdentifiedHandler<String, Command>>of(
                                new StartCommandHandler(sender),
                                new ListNotesCommandHandler(sender, notes),
                                new CreateNoteCommandHandler(sender, notes),
                                new EditNoteCommandHandler(sender, notes))
                        .toList();

        Handler<Command> commandsHandler =
                HandlerBuilder.from(
                                new SwitchHandler<>(
                                        commands,
                                        Processor.of(Command::name),
                                        Handler.of(new UnknownCommandHandler()::onCommand)))
                        .logging(INFO)
                        .named("CommandHandler")
                        .build();

        Handler<UpdateContext> commandUpdates =
                Processor.when(UpdateContext::isCommand)
                        .thenApply(UpdateContext::message)
                        .thenApply(Command::parse)
                        .thenHandle(commandsHandler);
        this.routes =
                Processor.of(UpdateContexts::parse)
                        .thenFilter(Objects::nonNull)
                        .thenHandle(
                                new MdcHandler<>(
                                        List.of(
                                                MdcEntry.of("chatId", UpdateContext::chatId),
                                                MdcEntry.of("userId", UpdateContext::userId)),
                                        commandUpdates));
    }

    @Override
    public Handled handle(Update update) {
        return routes.handle(update);
    }
}
