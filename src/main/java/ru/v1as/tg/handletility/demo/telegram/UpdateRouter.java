package ru.v1as.tg.handletility.demo.telegram;

import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.operation.handler.Handled;
import ru.operation.handler.Handler;
import ru.operation.processor.Processor;
import ru.v1as.tg.handletility.demo.model.Command;
import ru.v1as.tg.handletility.demo.model.UpdateContext;
import ru.v1as.tg.handletility.demo.model.UpdateContexts;
import ru.v1as.tg.handletility.demo.telegram.command.StartCommandHandler;

public final class UpdateRouter implements Handler<Update> {

    private final Handler<Update> routes;

    public UpdateRouter(TgSender sender) {
        Handler<UpdateContext> commandUpdates =
                Processor.when(UpdateContext::isCommand)
                        .thenApply(UpdateContext::message)
                        .thenApply(Command::parse)
                        .thenHandle(Handler.of(new StartCommandHandler(sender)::handle));
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
