package booking_bot.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface SendMessageService {
    void send(Long chatId, String message);
    void sendWithKeyboard(Long chatId, String message, List<String> buttons);
}
