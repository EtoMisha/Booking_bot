package booking_bot.commands;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class KeyboardMaker {

    public static ReplyKeyboardMarkup makeAdminKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(CommandNames.NEW_BOOKING.getText());
        keyboardRow1.add(CommandNames.MY_BOOKINGS.getText());

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(CommandNames.ADD_OBJECT.getText());
        keyboardRow2.add(CommandNames.EDIT_OBJECT.getText());
        keyboardRow2.add(CommandNames.EDIT_USER.getText());

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        keyBoardRows.add(keyboardRow2);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }

    public static ReplyKeyboardMarkup makeStudentKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(CommandNames.NEW_BOOKING.getText());
        keyboardRow1.add(CommandNames.MY_BOOKINGS.getText());

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }
}
