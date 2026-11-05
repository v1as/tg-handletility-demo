package ru.v1as.tg.handletility.demo.model;

import java.util.Objects;

public record NoteCallback(
        String data, CallbackAction action, Long noteId, long chatId, int messageId) {

    public NoteCallback {
        Objects.requireNonNull(data, "data");
    }

    public String chatIdStr() {
        return Long.toString(chatId);
    }

    public static NoteCallback parse(Callback callback) {
        String[] parts = callback.data().split("\\|", -1);
        if (parts.length < 2 || !"n".equals(parts[0])) {
            return unknown(callback);
        }
        return switch (parts[1]) {
            case "l" -> known(callback, CallbackAction.LIST, null);
            case "o" -> withId(callback, CallbackAction.OPEN, parts);
            case "d" -> withId(callback, CallbackAction.DELETE, parts);
            default -> unknown(callback);
        };
    }

    private static NoteCallback withId(Callback callback, CallbackAction action, String[] parts) {
        if (parts.length < 3) {
            return unknown(callback);
        }
        try {
            return known(callback, action, Long.parseLong(parts[2]));
        } catch (NumberFormatException exception) {
            return unknown(callback);
        }
    }

    private static NoteCallback known(Callback callback, CallbackAction action, Long noteId) {
        return new NoteCallback(
                callback.data(), action, noteId, callback.chatId(), callback.messageId());
    }

    private static NoteCallback unknown(Callback callback) {
        return known(callback, null, null);
    }
}
