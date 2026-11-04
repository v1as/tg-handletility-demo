package ru.v1as.tg.handletility.demo.note;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryNoteService implements NoteService {

    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<Long, Map<Long, Note>> notesByChat = new ConcurrentHashMap<>();

    @Override
    public Note create(long chatId, String title, String text) {
        long id = idSequence.getAndIncrement();
        Note note = new Note(id, chatId, title.trim(), text.trim());
        notesByChat.computeIfAbsent(chatId, ignored -> new ConcurrentHashMap<>()).put(id, note);
        return note;
    }

    @Override
    public List<Note> list(long chatId) {
        Map<Long, Note> chatNotes = notesByChat.get(chatId);
        if (chatNotes == null || chatNotes.isEmpty()) {
            return List.of();
        }
        return chatNotes.values().stream().sorted(Comparator.comparingLong(Note::id)).toList();
    }

    @Override
    public Optional<Note> find(long chatId, long noteId) {
        Map<Long, Note> chatNotes = notesByChat.get(chatId);
        if (chatNotes == null) {
            return Optional.empty();
        }
        Note note = chatNotes.get(noteId);
        if (note == null || note.chatId() != chatId) {
            return Optional.empty();
        }
        return Optional.of(note);
    }

    @Override
    public Optional<Note> updateText(long chatId, long noteId, String text) {
        Map<Long, Note> chatNotes = notesByChat.get(chatId);
        if (chatNotes == null) {
            return Optional.empty();
        }
        Note existing = chatNotes.get(noteId);
        if (existing == null || existing.chatId() != chatId) {
            return Optional.empty();
        }
        Note updated = existing.withText(text.trim());
        chatNotes.put(noteId, updated);
        return Optional.of(updated);
    }

    @Override
    public boolean delete(long chatId, long noteId) {
        Map<Long, Note> chatNotes = notesByChat.get(chatId);
        if (chatNotes == null) {
            return false;
        }
        Note removed = chatNotes.remove(noteId);
        return removed != null && removed.chatId() == chatId;
    }
}
