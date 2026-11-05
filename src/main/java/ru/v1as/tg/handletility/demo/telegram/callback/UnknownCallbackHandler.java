package ru.v1as.tg.handletility.demo.telegram.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.v1as.tg.handletility.demo.model.NoteCallback;

public final class UnknownCallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(UnknownCallbackHandler.class);

    public void handle(NoteCallback callback) {
        log.warn("Неизвестный callback {}", callback.data());
    }
}
