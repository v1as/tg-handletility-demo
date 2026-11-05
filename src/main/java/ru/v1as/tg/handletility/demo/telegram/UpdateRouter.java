package ru.v1as.tg.handletility.demo.telegram;

import static org.slf4j.event.Level.INFO;

import java.util.List;
import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.operation.MdcEntry;
import ru.operation.handler.Handled;
import ru.operation.handler.Handler;
import ru.operation.handler.HandlerBuilder;
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
        Handler<Command> commands =
                HandlerBuilder.from(
                                SwitchHandler.<String, Command>switchHandler()
                                        .handler(
                                                Handler.of(new StartCommandHandler(sender)::handle)
                                                        .identifiedBy("start"))
                                        .handler(
                                                Handler.of(
                                                                new ListNotesCommandHandler(
                                                                                sender, notes)
                                                                        ::handle)
                                                        .identifiedBy("notes"))
                                        .handler(
                                                Handler.of(
                                                                new CreateNoteCommandHandler(
                                                                                sender, notes)
                                                                        ::handle)
                                                        .identifiedBy("note"))
                                        .handler(
                                                Handler.of(
                                                                new EditNoteCommandHandler(
                                                                                sender, notes)
                                                                        ::handle)
                                                        .identifiedBy("edit"))
                                        .keyReader(
                                                Processor.of(Command::name)
                                                        .thenApply(String::toLowerCase))
                                        .defaultHandler(
                                                Handler.of(new UnknownCommandHandler()::handle))
                                        .build())
                        .logging(INFO)
                        .named("CommandHandler")
                        .build();
        Handler<UpdateContext> commandUpdates =
                Processor.when(UpdateContext::isCommand)
                        .thenApply(UpdateContext::message)
                        .thenApply(Command::parse)
                        .thenHandle(commands);
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
