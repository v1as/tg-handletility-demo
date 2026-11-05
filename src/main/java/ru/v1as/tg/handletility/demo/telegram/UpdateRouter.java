package ru.v1as.tg.handletility.demo.telegram;

import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.operation.handler.Handled;
import ru.operation.handler.Handler;
import ru.operation.handler.impl.switching.SwitchHandler;
import ru.operation.processor.Processor;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.model.UpdateContext;
import ru.v1as.tg.handletility.demo.model.UpdateContexts;
import ru.v1as.tg.handletility.demo.telegram.command.CreateNoteCommandHandler;
import ru.v1as.tg.handletility.demo.telegram.command.StartCommandHandler;

public final class UpdateRouter implements Handler<Update> {

    private final Handler<Update> routes;

    public UpdateRouter(TgSender sender) {
        StartCommandHandler start = new StartCommandHandler(sender);
        CreateNoteCommandHandler createNote = new CreateNoteCommandHandler(sender);

        Handler<Command> commands =
                SwitchHandler.<String, Command>switchHandler()
                        .handler(Handler.of(start::handle).identifiedBy("start"))
                        .handler(Handler.of(createNote::handle).identifiedBy("note"))
                        .keyReader(Processor.of(Command::name).thenApply(String::toLowerCase))
                        .build();
        Handler<UpdateContext> commandUpdates =
                Processor.when(UpdateContext::isCommand)
                        .thenApply(UpdateContext::message)
                        .thenApply(Command::parse)
                        .thenHandle(commands);
        this.routes =
                Processor.of(UpdateContexts::parse)
                        .thenFilter(Objects::nonNull)
                        .thenHandle(commandUpdates);
    }

    @Override
    public Handled handle(Update update) {
        return routes.handle(update);
    }
}
