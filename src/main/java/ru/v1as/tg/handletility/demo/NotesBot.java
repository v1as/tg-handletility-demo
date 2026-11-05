package ru.v1as.tg.handletility.demo;

import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.operation.handler.Handled;
import ru.v1as.tg.handletility.demo.note.InMemoryNoteService;
import ru.v1as.tg.handletility.demo.telegram.OkHttpTgSender;
import ru.v1as.tg.handletility.demo.telegram.TgSender;
import ru.v1as.tg.handletility.demo.telegram.UpdateRouter;

@Slf4j
public final class NotesBot implements LongPollingSingleThreadUpdateConsumer {

    private final UpdateRouter updates;

    public NotesBot(TgSender sender) {
        this.updates = new UpdateRouter(sender, new InMemoryNoteService());
    }

    public static void main(String[] args) throws Exception {
        String token = System.getenv("TG_BOT_TOKEN");
        if (token == null || token.isBlank()) {
            System.out.println("Задайте TG_BOT_TOKEN, чтобы запустить long polling");
            return;
        }
        TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close(application)));
        application.registerBot(token, new NotesBot(new OkHttpTgSender(token)));
        new CountDownLatch(1).await();
    }

    public Handled handle(Update update) {
        return updates.handle(update);
    }

    @Override
    public void consume(Update update) {
        handle(update);
    }

    private static void close(TelegramBotsLongPollingApplication application) {
        try {
            application.close();
        } catch (Exception e) {
            log.warn("Не удалось остановить long polling", e);
        }
    }
}
