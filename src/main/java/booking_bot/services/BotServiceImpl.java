package booking_bot.services;

import booking_bot.Bot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
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
        send.setReplyMarkup(inlineKeyboard(buttons));
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

    private InlineKeyboardMarkup inlineKeyboard(List<String> buttonsList) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();

        for (String buttonText : buttonsList) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();

            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonText);
            keyboardButtonRow.add(button);
            totalList.add(keyboardButtonRow);
        }
        inlineKeyboardMarkup.setKeyboard(totalList);

        return inlineKeyboardMarkup;
    }

}
