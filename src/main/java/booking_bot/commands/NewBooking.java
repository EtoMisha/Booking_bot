package booking_bot.commands;

import booking_bot.models.*;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import org.springframework.dao.DataAccessException;
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
    private Booking booking;
    private User user;

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

        user = controller.getUser().findByTelegram(chatId);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
        List<String> buttons;

        if (status.equals("begin")) {
            isFinished = false;
            //TODO показывать только категории внутри кампуса, придутся в таблицу types добавить ссылку
            buttons = getNames(controller.getType().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);
            statusMap.put(chatId, "Выбор объекта");

        } else if (status.equals("Выбор объекта")) {

            List<BookObject> bookObjectList = controller.getBookingObject().findByType(input);

            if (bookObjectList.isEmpty()) {
                sendMessageService.send(chatId, "Ничего не нашлось");
                statusMap.put(chatId, "begin");

            } else {
                sendMessageService.send(chatId, "Выберите что забронировать:");
                for (BookObject object : bookObjectList) {
                    if (object.getImage().equals("null")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId.toString());
                        sendMessage.setParseMode("markdown");
                        if (object.getDescription().equals("null")) {
                            sendMessage.setText("*" + object.getName() + "*");
                        } else {
                            sendMessage.setText("*" + object.getName() + "*\n" + object.getDescription());
                        }
                        sendMessage.setReplyMarkup(makeBookButton(object.getName()));
                        System.out.println("NAME " + object.getName());
                        sendMessageService.sendCustom(sendMessage);
                    } else {
                        SendPhoto sendPhoto = makeObjectMessage(object);
                        sendMessageService.sendPhoto(sendPhoto);
                    }
                }

                statusMap.put(chatId, "Запрос даты");
            }


        } else if (this.status.equals("Запрос даты")) {

            bookObject = controller.getBookingObject().findByName(input);
            System.out.println("new booking " + bookObject);
            SendMessage send = new SendMessage();

            send.setChatId(chatId.toString());
            send.setText("Выберите дату бронирования:");
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
                if (selectedDate.isEqual(LocalDate.now()) && timeStart.isBefore(LocalTime.now())) {
                    sendMessageService.send(chatId, "Время должно быть не раньше текущего.\nВведите время начала снова");
                    statusMap.put(chatId, "Ввод времени начала");
                } else {
                    sendMessageService.send(chatId, "Введите время окончания, тоже в формате ЧЧ.ММ");
                    statusMap.put(chatId, "Ввод времени окончания");
                }
            } catch (DateTimeParseException var8) {
                System.err.println(var8.getMessage());
                sendMessageService.send(chatId, "Не получилось, проверьте что формат времени правильный: ЧЧ.ММ\nНапример 12.30");
            }

        } else if (status.equals("Ввод времени окончания")) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            try {
                timeEnd = LocalTime.parse(input, timeFormatter);
                if (timeEnd.isBefore(timeStart)) {
                    sendMessageService.send(chatId, "Время окончания не должно быть раньше времени начала.\nВведите время окончания снова");
                    statusMap.put(chatId, "Ввод времени окончания");
                } else if (!checkSlot(timeStart, timeEnd)) {
                    sendMessageService.send(chatId, "Ваше время пересекается с уже имеющимся слотом.\nВведите время начала снова");
                    statusMap.put(chatId, "Ввод времени начала");
                } else {

                        booking.setBookObject(bookObject);
                        booking.setTimeStart(LocalDateTime.of(selectedDate, timeStart));
                        booking.setTimeEnd(LocalDateTime.of(selectedDate, timeEnd));
                        booking.setStatus(controller.getStatus().findByName("Занят"));

                        booking.setUser(user);

                        try {
                            controller.getBooking().save(booking);
                            sendMessageService.send(chatId, "Готово \uD83D\uDC4D \nЗабронировали на "
                                    + selectedDate.format(dateFormatter) + " с " + timeStart + " до " + timeEnd
                                    + ":\n" + bookObject.getName());
                        } catch (DataAccessException e) {
                            e.printStackTrace();
                            sendMessageService.send(chatId, "Не получилось, ошибка в базе данных :(");
                        }

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

                if (counter.isBefore(today)) {
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

    private String getBookedSlots(LocalDate date) {
        List<Booking> bookingList = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            bookingList = controller.getBooking().findByObject(bookObject);
            for (Booking booking: bookingList) {
                if (booking.getTimeStart().toLocalDate().equals(date)) {
                    stringBuilder.append(booking.getTimeStart().toLocalTime())
                            .append(" - ")
                            .append(booking.getTimeEnd().toLocalTime())
                            .append("\n");
                }
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        if (bookingList == null) {
            return null;
        } else {
            return stringBuilder.toString();
        }
    }

    private boolean checkSlot(LocalTime timeStart, LocalTime timeEnd) {
        LocalTime start;
        LocalTime end;

        List<Booking> bookingList = controller.getBooking().findByObject(bookObject);
        for (Booking booking : bookingList) {
            start = booking.getTimeStart().toLocalTime();
            end = booking.getTimeEnd().toLocalTime();
            if (timeStart.isBefore(end) || timeEnd.isBefore(start)) {
                System.out.println("start " + start);
                System.out.println("end " + end);
                System.out.println("selected: " + timeStart + " " + timeEnd);
                return false;
            }
        }

        return true;
    }

    private SendPhoto makeObjectMessage(Object object) {
        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setParseMode("markdown");
        BookObject bookObject = (BookObject) object;
        String objectText = "*" + bookObject.getName() + "*\n"
                + bookObject.getDescription();
        sendPhoto.setCaption(objectText);
        sendPhoto.setPhoto(new InputFile(new File(bookObject.getImage())));
        sendPhoto.setReplyMarkup(makeBookButton(bookObject.getName()));

        return sendPhoto;
    }

    private InlineKeyboardMarkup makeBookButton(String callBackText) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("Забронировать");
        button.setCallbackData(callBackText);
        keyboardButtonRow.add(button);
        totalList.add(keyboardButtonRow);
        inlineKeyboardMarkup.setKeyboard(totalList);

        return inlineKeyboardMarkup;
    }


}
