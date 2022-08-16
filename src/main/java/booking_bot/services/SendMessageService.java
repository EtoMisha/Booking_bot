package booking_bot.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface SendMessageService {
    void send(Long chatId, String message);
    void sendCustom(SendMessage send);
}
