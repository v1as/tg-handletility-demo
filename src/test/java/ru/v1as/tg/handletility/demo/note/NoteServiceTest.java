package ru.v1as.tg.handletility.demo.note;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NoteServiceTest {

    private final NoteService notes = new InMemoryNoteService();

    @Test
    void createsListsUpdatesAndDeletesInsideOneChat() {
        Note first = notes.create(1L, "A", "one");
        Note second = notes.create(1L, "B", "two");

        assertEquals(List.of(first, second), notes.list(1L));

        Optional<Note> updated = notes.updateText(1L, first.id(), "changed");
        assertEquals(Optional.of(new Note(first.id(), 1L, "A", "changed")), updated);
        assertTrue(notes.delete(1L, second.id()));
        assertEquals(List.of(updated.orElseThrow()), notes.list(1L));
        assertFalse(notes.delete(1L, second.id()));
    }

    @Test
    void doesNotShowNotesOfAnotherChat() {
        Note note = notes.create(1L, "A", "one");

        assertEquals(List.of(), notes.list(2L));
        assertTrue(notes.find(2L, note.id()).isEmpty());
        assertTrue(notes.updateText(2L, note.id(), "nope").isEmpty());
        assertFalse(notes.delete(2L, note.id()));
    }
}
