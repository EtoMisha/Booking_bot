package booking_bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Bot extends TelegramLongPollingBot {

    private final String USERNAME;
    private final String TOKEN;
    private final Handler handler;

    private final String newSlot = "Новый слот";
    private final String removeSlot = "Удалить слот";
    private final String show = "Посмотреть записи";
    private final String setupSlots = "Настроить слоты";
    private final String setupNotifications = "Настроить уведомления";


    private Status status;

    private ReplyKeyboardMarkup replyKeyboard;

    public Bot(String username, String token, Handler handler) {
        this.USERNAME = username;
        this.TOKEN = token;
        this.handler = handler;
        initKeyboard();
        status = Status.COMMON;
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String message = update.getMessage().getText().trim();

            if (status == Status.NEW_SLOT_DATE) {
                sendMsg(chatId, handler.newSlotDate(message));
                status = Status.NEW_SLOT_TIME;

            } else if (status == Status.NEW_SLOT_TIME) {
                sendMsg(chatId, handler.newSlotTime(message));
                status = Status.COMMON;
                System.out.println(status);
            } else {
                getCommonAnswer(chatId, message);
            }
        }

    }

    void sendMsg(String chatId, String message) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId);
        send.setText(message);
        ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard(true);

        send.setReplyMarkup(replyKeyboard);

        try {
            execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getCommonAnswer(String chatId, String message) {
        System.out.println("message: " + message);

        switch (message) {
            case ("/start"):
                sendMsg(chatId, "Привет. Это админка бота");
                break;
            case (newSlot):
                status = Status.NEW_SLOT_DATE;
                sendMsg(chatId, "Введите дату в формате ДД.ММ.ГГГГ, например \"11.08.2022\"");
                break;
            default:
                sendMsg(chatId, "Неизвестная команда");
                break;
        }
    }

    void initKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(newSlot);
        keyboardRow1.add(removeSlot);
        keyboardRow1.add(show);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(setupSlots);
        keyboardRow2.add(setupNotifications);

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        keyBoardRows.add(keyboardRow2);

        replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);
    }

    enum Status {
        COMMON, NEW_SLOT_DATE, NEW_SLOT_TIME, REMOVE_SLOT, SETUP_SLOT, SETUP_NOTIF
    }
}