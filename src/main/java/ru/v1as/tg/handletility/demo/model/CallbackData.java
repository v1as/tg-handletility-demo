package ru.v1as.tg.handletility.demo.model;

public final class CallbackData {

    private CallbackData() {}

    public static String list() {
        return "n|l";
    }

    public static String open(long noteId) {
        return "n|o|" + noteId;
    }

    public static String delete(long noteId) {
        return "n|d|" + noteId;
    }
}
