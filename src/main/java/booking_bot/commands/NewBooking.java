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
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class NewBooking extends CommandParent {
    private String commandName = "Забронировать";

    private LocalDate selectedDate;
    private LocalTime timeStart;
    private LocalTime timeEnd;

    public NewBooking(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        super(sendMessageService, repository, commandContainer);
        commandContainer.add(commandName, this);
    }

    public boolean execute(Update update, boolean begin) {
        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }


        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
        List<String> buttons;

        if (status.equals("begin")) {
            isFinished = false;
            buttons = new ArrayList<>();
            buttons.add("Помещения");
            buttons.add("Настолки");
            buttons.add("Спортинвентарь");
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);
            statusMap.put(chatId, "Выбор объекта");

        } else if (status.equals("Выбор объекта")) {
            buttons = new ArrayList<>();
            buttons.add("Переговорка");
            buttons.add("Игровая");
            buttons.add("Спортинвентарь");
            sendMessageService.sendWithKeyboard(chatId, "Выберите что забронировать", buttons);
            statusMap.put(chatId, "Ввод даты");

        } else if (this.status.equals("Ввод даты")) {
            SendMessage send = new SendMessage();
            send.setChatId(chatId.toString());
            send.setText("Выберите дату");
            send.setReplyMarkup(makeCalendar());
            sendMessageService.sendCustom(send);
            statusMap.put(chatId, "Запрос времени");

        } else if (status.equals("Запрос времени")) {

            if (input.equals("x")) {
                sendMessageService.send(chatId, "На этот день не получится, выберите другой");
            } else {
                selectedDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), Integer.parseInt(input));
                String bookedSlots = getBookedSlots(selectedDate);
                sendMessageService.send(chatId, "На эту дату уже есть такие бронирования:\n" + bookedSlots);

                sendMessageService.send(chatId, "Введите время начала в формате ЧЧ.ММ\nНапример 12.30");
                statusMap.put(chatId, "Ввод времени начала");
            }

        } else if (status.equals("Ввод времени начала")) {
            try {
                timeStart = LocalTime.parse(input, timeFormatter);
                sendMessageService.send(chatId, "Введите время окончания, тоже в формате ЧЧ.ММ");
                statusMap.put(chatId, "Ввод времени окончания");
            } catch (DateTimeParseException var8) {
                System.err.println(var8.getMessage());
                sendMessageService.send(chatId, "Не получилось, проверьте что формат времени правильный: ЧЧ.ММ\nНапример 12.30");
            }

        } else if (status.equals("Ввод времени окончания")) {

            try {
                timeEnd = LocalTime.parse(input, timeFormatter);
                if (timeEnd.isBefore(timeStart)) {
                    sendMessageService.send(chatId, "Время окончания должно быть раньше времени начала.\nВведите время окончания снова");
                    statusMap.put(chatId, "Ввод времени окончания");
                } else {
                    sendMessageService.send(chatId, "Готово. Забронировали с " + timeStart + " до " + timeEnd);
                    statusMap.put(chatId, "begin");
                    isFinished = true;
                }

            } catch (DateTimeParseException var7) {
                System.err.println(var7.getMessage());
                sendMessageService.send(chatId, "Не получилось, проверьте что формат времени правильный: ЧЧ.ММ\nНапример 12.30");
            }

        }

        return isFinished;
    }

    public String getCommandName() {
        return commandName;
    }

    private InlineKeyboardMarkup makeCalendar() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate counter = today.minusDays(today.getDayOfWeek().getValue());

        for(int i = 0; i < 4; ++i) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();

            for(int j = 0; j < 7; ++j) {
                InlineKeyboardButton button = new InlineKeyboardButton();

                if (counter.isBefore(today) || !isFree(counter)) {
                    button.setText("X");
                    button.setCallbackData("x");
                } else {
                    button.setText(String.valueOf(counter.getDayOfMonth()));
                    button.setCallbackData(String.valueOf(counter.getDayOfMonth()));
                }

                keyboardButtonRow.add(button);
                counter = counter.plusDays(1);

            }

            totalList.add(keyboardButtonRow);
        }

        inlineKeyboardMarkup.setKeyboard(totalList);
        return inlineKeyboardMarkup;
    }

    private boolean isFree(LocalDate date) {
        return  true;
    }

    private String getBookedSlots(LocalDate date) {
        return  null;
    }
}
