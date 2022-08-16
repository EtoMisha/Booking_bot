package booking_bot.services;

import booking_bot.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

public class SendMessageServiceImpl implements SendMessageService {

    private final Bot bot;

    public SendMessageServiceImpl(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void send(Long chatId, String message) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId.toString());
        send.setText(message);
        send.setReplyMarkup(initKeyboard());

//        send.enableMarkdown(true);

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

    ReplyKeyboardMarkup initKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("/start");
        keyboardRow1.add("тест");

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Кнопка 3");
        keyboardRow2.add("Кнопка 4");

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        keyBoardRows.add(keyboardRow2);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }
}
