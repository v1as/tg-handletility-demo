package ru.v1as.tg.handletility.demo.telegram.callback;

import java.util.List;
import ru.operation.handler.AbstractHandler;
import ru.operation.handler.Handled;
import ru.operation.handler.IdentifiedHandler;
import ru.v1as.tg.handletility.demo.model.CallbackAction;
import ru.v1as.tg.handletility.demo.model.NoteCallback;
import ru.v1as.tg.handletility.demo.note.Note;
import ru.v1as.tg.handletility.demo.telegram.NotesListFormat;

public abstract class AbstractNoteCallbackHandler extends AbstractHandler<NoteCallback>
        implements IdentifiedHandler<CallbackAction, NoteCallback> {

    private final CallbackAction action;

    protected AbstractNoteCallbackHandler(CallbackAction action) {
        this.action = action;
    }

    @Override
    public CallbackAction identifier() {
        return action;
    }

    @Override
    protected Handled handleInternal(NoteCallback callback) throws Exception {
        onCallback(callback);
        return Handled.done();
    }

    protected abstract void onCallback(NoteCallback callback) throws Exception;

    protected final String notesListText(List<Note> notes) {
        return NotesListFormat.format(notes);
    }
}
