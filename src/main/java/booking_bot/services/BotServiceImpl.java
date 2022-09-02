package booking_bot.services;

import booking_bot.Bot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;

public class BotServiceImpl implements BotService {

    private final Bot bot;

    public BotServiceImpl(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void sendMessage(Long chatId, String message, List<String> buttons) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId.toString());
        send.setText(message);
        send.setParseMode("markdown");
       
        try {
            bot.execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCustom(SendMessage send) {

        try {
            bot.execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPhoto(SendPhoto sendPhoto) {
        try {
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadPhoto(GetFile getFile, File file) throws TelegramApiException {
        bot.downloadFile(bot.execute(getFile), file);
    }

}
