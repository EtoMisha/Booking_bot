package ru.booking.bot.command;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.booking.bot.models.User;

import java.util.ArrayList;

public class KeyboardMaker {
    public static ReplyKeyboardMarkup getKeyboard(User user) {
        return user.isAdmin() ? getAdminKeyboard() : getCustomerKeyboard();
    }

    public static ReplyKeyboardMarkup getAdminKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(CommandName.NEW_BOOKING.getText());
        keyboardRow1.add(CommandName.MY_BOOKINGS.getText());

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(CommandName.ADD_OBJECT.getText());
        keyboardRow2.add(CommandName.EDIT_OBJECT.getText());

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        keyBoardRows.add(keyboardRow2);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }

    public static ReplyKeyboardMarkup getCustomerKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(CommandName.NEW_BOOKING.getText());
        keyboardRow1.add(CommandName.MY_BOOKINGS.getText());

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }
}
