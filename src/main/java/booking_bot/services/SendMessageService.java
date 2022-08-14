package booking_bot.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface SendMessageService {
    void send(Long chatId, String message);
//    void sendWithKeyboard(String chatId, String message);
    void sendWithButton(Long chatId, String message, String button);

    void sendCustom(SendMessage send);
}
