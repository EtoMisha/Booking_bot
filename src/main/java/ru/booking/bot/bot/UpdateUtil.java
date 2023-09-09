package ru.booking.bot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateUtil {
    public static long getUserId(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();
    }

    public static String getInput(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getData()
                : update.getMessage().getText();
    }
}
