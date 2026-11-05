package ru.v1as.tg.handletility.demo.telegram.callback;

import ru.v1as.tg.handletility.demo.model.NoteCallback;

@FunctionalInterface
public interface NoteCallbackHandler {

    void handle(NoteCallback callback) throws Exception;
}
