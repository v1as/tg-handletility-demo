package ru.v1as.tg.handletility.demo.telegram.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.v1as.tg.handletility.demo.model.Command;

public final class UnknownCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UnknownCommandHandler.class);

    public void handle(Command command) {
        log.warn("Неизвестная команда /{}", command.name());
    }
}
