package booking_bot.services;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;

public interface BotService {
    void sendMessage(Long chatId, String message, List<String> buttons);
    void sendCustom(SendMessage send);
    void sendPhoto(SendPhoto sendPhoto);
    void downloadPhoto(GetFile getFile, File file) throws TelegramApiException;

}
