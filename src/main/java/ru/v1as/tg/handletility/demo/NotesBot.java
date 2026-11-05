package ru.v1as.tg.handletility.demo;

import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public final class NotesBot implements LongPollingSingleThreadUpdateConsumer {

    public static void main(String[] args) throws Exception {
        String token = System.getenv("TG_BOT_TOKEN");
        if (token == null || token.isBlank()) {
            System.out.println("Задайте TG_BOT_TOKEN, чтобы запустить long polling");
            return;
        }
        TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close(application)));
        application.registerBot(token, new NotesBot());
        new CountDownLatch(1).await();
    }

    @Override
    public void consume(Update update) {
        log.debug("Update {} ignored", update.getUpdateId());
    }

    private static void close(TelegramBotsLongPollingApplication application) {
        try {
            application.close();
        } catch (Exception e) {
            log.warn("Не удалось остановить long polling", e);
        }
    }
}
