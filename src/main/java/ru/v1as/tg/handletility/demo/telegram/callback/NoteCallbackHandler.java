package ru.v1as.tg.handletility.demo.telegram.callback;

import ru.v1as.tg.handletility.demo.model.CallbackAction;
import ru.v1as.tg.handletility.demo.model.NoteCallback;

public interface NoteCallbackHandler {

    CallbackAction getAction();

    void onCallback(NoteCallback callback) throws Exception;
}
