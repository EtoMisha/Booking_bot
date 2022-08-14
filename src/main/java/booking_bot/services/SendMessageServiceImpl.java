package booking_bot.services;

import booking_bot.Bot;
import booking_bot.commands.CommandNames;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
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

        send.enableMarkdown(true);

        try {
            bot.execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendWithButton(Long chatId, String message, String button) {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(button);
        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        SendMessage send = new SendMessage();
        send.setChatId(chatId.toString());
        send.setText(message);
        send.setReplyMarkup(replyKeyboard);

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
        keyboardRow1.add(CommandNames.NEW_SLOT.getText());
        keyboardRow1.add(CommandNames.REMOVE_SLOT.getText());
        keyboardRow1.add(CommandNames.SHOW_SLOTS.getText());

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(CommandNames.SETUP_SLOTS.getText());
        keyboardRow2.add(CommandNames.SETUP_NOTIFICATIONS.getText());

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        keyBoardRows.add(keyboardRow2);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }
}
