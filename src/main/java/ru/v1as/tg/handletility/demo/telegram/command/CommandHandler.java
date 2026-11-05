package ru.v1as.tg.handletility.demo.telegram.command;

import ru.v1as.tg.handletility.demo.model.Command;

public interface CommandHandler {

    String getName();

    void onCommand(Command command) throws Exception;
}
