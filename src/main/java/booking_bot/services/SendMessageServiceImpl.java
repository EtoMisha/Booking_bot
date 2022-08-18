package booking_bot.services;

import booking_bot.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

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
        send.setReplyMarkup(replyKeyboard());

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
    public void sendWithKeyboard(Long chatId, String message, List<String> buttons) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId.toString());
        send.setText(message);
        send.setReplyMarkup(inlineKeyboard(buttons));

        try {
            bot.execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup replyKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Забронировать");
        keyboardRow1.add("Отмена бронирования");

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Добавить объект");
//        keyboardRow2.add("Редактировать каталог");
        keyboardRow2.add("Редактировать объект");
        keyboardRow2.add("Управление пользователем");

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        keyBoardRows.add(keyboardRow2);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
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
