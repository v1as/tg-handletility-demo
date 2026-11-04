package ru.v1as.tg.handletility.demo.note;

import java.util.Objects;

public record Note(long id, long chatId, String title, String text) {

    public Note {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(text, "text");
    }

    public Note withText(String newText) {
        return new Note(id, chatId, title, newText);
    }
}
