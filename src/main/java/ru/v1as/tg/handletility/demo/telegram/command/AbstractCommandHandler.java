package ru.v1as.tg.handletility.demo.telegram.command;

import ru.operation.handler.AbstractHandler;
import ru.operation.handler.Handled;
import ru.operation.handler.IdentifiedHandler;
import ru.v1as.tg.handletility.demo.model.Command;

public abstract class AbstractCommandHandler extends AbstractHandler<Command>
        implements IdentifiedHandler<String, Command> {

    private final String name;

    protected AbstractCommandHandler(String name) {
        this.name = name;
    }

    @Override
    public String identifier() {
        return name;
    }

    @Override
    protected Handled handleInternal(Command command) throws Exception {
        onCommand(command);
        return Handled.done();
    }

    protected abstract void onCommand(Command command) throws Exception;
}
