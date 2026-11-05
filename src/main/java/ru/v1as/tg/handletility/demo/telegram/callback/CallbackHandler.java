package ru.v1as.tg.handletility.demo.telegram.callback;

import ru.v1as.tg.handletility.demo.model.Callback;

@FunctionalInterface
public interface CallbackHandler {

    void handle(Callback callback) throws Exception;
}
