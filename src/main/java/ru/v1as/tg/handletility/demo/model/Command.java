package ru.v1as.tg.handletility.demo.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public record Command(
        String name, String botName, List<String> arguments, long chatId, long userId) {

    private static final Pattern PATTERN =
            Pattern.compile("/([\\w\\d_]+)@?([\\w\\d_]+)?(\\s)?(.*)", Pattern.DOTALL);

    public Command {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(botName, "botName");
        Objects.requireNonNull(arguments, "arguments");
    }

    public static Command parse(Message message) {
        String text = message.getText() == null ? "" : message.getText();
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unsupported command format: " + text);
        }
        String botName = matcher.group(2) == null ? "" : matcher.group(2);
        String rawArguments = matcher.group(4) == null ? "" : matcher.group(4);
        return new Command(
                matcher.group(1),
                botName,
                split(rawArguments),
                message.getChatId(),
                message.getFrom().getId());
    }

    private static List<String> split(String arguments) {
        return Arrays.stream(arguments.split("[ ;_,\\t\\r\\n]"))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .toList();
    }
}
