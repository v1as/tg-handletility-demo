package ru.v1as.tg.handletility.demo.telegram.callback;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import ru.v1as.tg.handletility.demo.model.Callback;
import ru.v1as.tg.handletility.demo.telegram.TgSender;

@RequiredArgsConstructor
public final class AnswerCallbackHandler implements CallbackHandler {

    private final TgSender sender;

    @Override
    public void handle(Callback callback) {
        sender.execute(
                AnswerCallbackQuery.builder().callbackQueryId(callback.callbackQueryId()).build());
    }
}
