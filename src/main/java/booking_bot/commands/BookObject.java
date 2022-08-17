package booking_bot.commands;

import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BookObject extends CommandParent {
    private String commandName = "Забронировать";

    public BookObject(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        super(sendMessageService, repository, commandContainer);
        commandContainer.add(this.commandName, this);
    }

    public boolean execute(Update update, boolean begin) {
        this.prepare(update);
        if (begin) {
            this.status = "begin";
            this.statusMap.put(this.chatId, this.status);
        }

        LocalTime timeStart = null;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
        ArrayList buttons;
        if (this.status.equals("begin")) {
            this.isFinished = false;
            buttons = new ArrayList();
            buttons.add("Помещения");
            buttons.add("Настолки");
            buttons.add("Спортинвентарь");
            this.sendMessageService.sendWithKeyboard(this.chatId, "Выберите категорию", buttons);
            this.statusMap.put(this.chatId, "Выбор объекта");
        } else if (this.status.equals("Выбор объекта")) {
            buttons = new ArrayList();
            buttons.add("Переговорка");
            buttons.add("Игровая");
            buttons.add("Спортинвентарь");
            this.sendMessageService.sendWithKeyboard(this.chatId, "Выберите что забронировать", buttons);
            this.statusMap.put(this.chatId, "Ввод даты");
        } else if (this.status.equals("Ввод даты")) {
            SendMessage send = new SendMessage();
            send.setChatId(this.chatId.toString());
            send.setText("Выберите дату");
            send.setReplyMarkup(this.makeCalendar());
//            this.sendMessageService.sendCustom(send);
            this.statusMap.put(this.chatId, "Запрос времени");
        } else if (this.status.equals("Запрос времени")) {
            this.sendMessageService.send(this.chatId, "С какого времени забронировать? Введите в формате ЧЧ.ММ\nНапример 12.30");
            this.statusMap.put(this.chatId, "Ввод времени начала");
        } else if (this.status.equals("Ввод времени начала")) {
            try {
                LocalTime.parse(this.input, timeFormatter);
                this.sendMessageService.send(this.chatId, "Ок, и до какого времени? Тоже в формате ЧЧ.ММ");
                this.statusMap.put(this.chatId, "Ввод времени окончания");
            } catch (DateTimeParseException var8) {
                System.err.println(var8.getMessage());
                this.sendMessageService.send(this.chatId, "Не получилось, проверьте что формат даты правильный");
            }
        } else if (this.status.equals("Ввод времени окончания")) {
            try {
                LocalTime timeEnd = LocalTime.parse(this.input, timeFormatter);
                this.sendMessageService.send(this.chatId, "Готово. Забронировали с " + timeStart + " до " + timeEnd);
                this.statusMap.put(this.chatId, "begin");
                this.isFinished = true;
            } catch (DateTimeParseException var7) {
                System.err.println(var7.getMessage());
                this.sendMessageService.send(this.chatId, "Не получилось, проверьте что формат даты правильный");
            }
        }

        return this.isFinished;
    }

    public String getCommandName() {
        return this.commandName;
    }

    private InlineKeyboardMarkup makeCalendar() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int day = dayOfWeek.getValue();
        System.out.println("calendar: day of week " + day);
        int counter = 0;

        for(int i = 0; i < 4; ++i) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList();

            for(int j = 0; j < 7; ++j) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("X");
                button.setCallbackData("X");
                keyboardButtonRow.add(button);
                ++counter;
            }

            totalList.add(keyboardButtonRow);
        }

        inlineKeyboardMarkup.setKeyboard(totalList);
        return inlineKeyboardMarkup;
    }
}
