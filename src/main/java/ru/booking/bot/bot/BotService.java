package ru.booking.bot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

public interface BotService {

    void sendText(Long chatId, String text);
    void sendWithKeyboard(Long chatId, String text, List<Button> buttons);
    void sendMarkup(Long chatId, String text, ReplyKeyboard markup);
    void sendPhoto(SendPhoto sendPhoto, List<Button> buttons);
    String downloadPhoto(Update update) throws TelegramApiException;

}
