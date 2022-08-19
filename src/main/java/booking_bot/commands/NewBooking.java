package booking_bot.commands;

import booking_bot.models.BookObject;
import booking_bot.models.Booking;
import booking_bot.models.Status;
import booking_bot.models.Type;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.awt.*;
import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class NewBooking extends CommandParent {
    private String commandName = "Забронировать";

    private LocalDate selectedDate;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private BookObject bookObject;
    Booking booking;

    public NewBooking(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        super(sendMessageService, controller, commandContainer);
        commandContainer.add(commandName, this);
        bookObject = new BookObject();
        booking = new Booking();
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
            buttons = getNames(controller.getType().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);
            statusMap.put(chatId, "Выбор объекта");

        } else if (status.equals("Выбор объекта")) {

            List<BookObject> bookObjectList = controller.getBookingObject().findByType(input);

            sendMessageService.send(chatId, "Выберите что забронировать:");


            for (Object object : bookObjectList) {
                SendPhoto sendPhoto = makeObjectMessage(object);
                sendMessageService.sendPhoto(sendPhoto);
            }

            statusMap.put(chatId, "Запрос даты");
        } else if (this.status.equals("Запрос даты")) {

            bookObject = controller.getBookingObject().findByName(input);
            System.out.println("new booking" + bookObject);
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

                if (bookedSlots == null) {
                    sendMessageService.send(chatId, "На эту дату всё свободно");
                } else {
                    sendMessageService.send(chatId, "На эту дату уже есть такие бронирования:\n" + bookedSlots);
                }

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

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            try {
                timeEnd = LocalTime.parse(input, timeFormatter);
                if (timeEnd.isBefore(timeStart)) {
                    sendMessageService.send(chatId, "Время окончания должно быть раньше времени начала.\nВведите время окончания снова");
                    statusMap.put(chatId, "Ввод времени окончания");
                } else {
                    booking.setBookObject(bookObject);
                    booking.setTimeStart(LocalDateTime.of(selectedDate, timeStart));
                    booking.setTimeEnd(LocalDateTime.of(selectedDate, timeEnd));
                    booking.setStatus(controller.getStatus().findByName("Занят"));

                    sendMessageService.send(chatId, "Готово. Забронировали на " + selectedDate.format(dateFormatter) + " с " + timeStart + " до " + timeEnd);

                    controller.getBooking().save(booking);
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

    private SendPhoto makeObjectMessage(Object object) {
//        SendMessage send = new SendMessage();
        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setParseMode("markdown");
        BookObject bookObject = (BookObject) object;
        String objectText = "*" + bookObject.getName() + "*\n"
                + bookObject.getDescription();
        sendPhoto.setCaption(objectText);

        sendPhoto.setPhoto(new InputFile(new File("meme.jpg")));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("Забронировать");
        button.setCallbackData(bookObject.getName());
        keyboardButtonRow.add(button);
        totalList.add(keyboardButtonRow);
        inlineKeyboardMarkup.setKeyboard(totalList);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);

        return sendPhoto;
    }


}
