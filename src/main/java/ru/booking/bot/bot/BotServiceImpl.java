package ru.booking.bot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BotServiceImpl implements BotService {

    private final Bot bot;

    @Value("${bot.images-dir}")
    private String imagesDir;

    @Override
    public void sendText(Long chatId, String text) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId.toString());
        send.setText(text);
        send.setParseMode(ParseMode.MARKDOWN);
        try {
            bot.execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendWithKeyboard(Long chatId, String text, List<Button> buttons) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId.toString());
        send.setText(text);
        send.setReplyMarkup(inlineKeyboard(buttons));
        try {
            bot.execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMarkup(Long chatId, String text, ReplyKeyboard markup) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId.toString());
        send.setText(text);
        send.setParseMode(ParseMode.MARKDOWN);
        send.setReplyMarkup(markup);
        try {
            bot.execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPhoto(SendPhoto sendPhoto, List<Button> buttons) {
        sendPhoto.setReplyMarkup(inlineKeyboard(buttons));
        sendPhoto.setParseMode(ParseMode.MARKDOWN);

        try {
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String downloadPhoto(Update update) throws TelegramApiException {
        String fileId = update.getMessage().getPhoto().get(2).getFileId();
        String filePath = imagesDir + "/" + fileId + ".jpeg";
        bot.downloadFile(bot.execute(new GetFile(fileId)), new File(filePath));
        return filePath;
    }

    private InlineKeyboardMarkup inlineKeyboard(List<Button> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();

        for (Button button : buttons) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();

            InlineKeyboardButton inlineButton = new InlineKeyboardButton(button.getLabel());
            inlineButton.setCallbackData(button.getCallBack());
            keyboardButtonRow.add(inlineButton);
            totalList.add(keyboardButtonRow);
        }
        inlineKeyboardMarkup.setKeyboard(totalList);

        return inlineKeyboardMarkup;
    }

}
