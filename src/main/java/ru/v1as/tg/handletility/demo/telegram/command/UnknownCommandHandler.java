package ru.v1as.tg.handletility.demo.telegram.command;

import lombok.extern.slf4j.Slf4j;
import ru.v1as.tg.handletility.demo.model.Command;

@Slf4j
public final class UnknownCommandHandler {

    public void onCommand(Command command) {
        log.warn("Неизвестная команда /{}", command.name());
    }
}
