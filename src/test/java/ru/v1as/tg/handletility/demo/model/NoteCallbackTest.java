package ru.v1as.tg.handletility.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class NoteCallbackTest {

    @Test
    void readsKnownActions() {
        NoteCallback list = NoteCallback.parse(callback(CallbackData.list()));
        assertEquals(CallbackAction.LIST, list.action());
        assertNull(list.noteId());

        NoteCallback open = NoteCallback.parse(callback(CallbackData.open(5)));
        assertEquals(CallbackAction.OPEN, open.action());
        assertEquals(5L, open.noteId());

        NoteCallback delete = NoteCallback.parse(callback(CallbackData.delete(7)));
        assertEquals(CallbackAction.DELETE, delete.action());
        assertEquals(7L, delete.noteId());
    }

    @Test
    void keepsUnknownDataWithoutAction() {
        NoteCallback callback = NoteCallback.parse(callback("other|x"));

        assertEquals("other|x", callback.data());
        assertNull(callback.action());
        assertNull(callback.noteId());
    }

    @Test
    void keepsNonNumericIdWithoutAction() {
        NoteCallback callback = NoteCallback.parse(callback("n|o|nope"));

        assertNull(callback.action());
        assertNull(callback.noteId());
    }

    private static Callback callback(String data) {
        return new Callback(data, 10L, 4, "cb");
    }
}
