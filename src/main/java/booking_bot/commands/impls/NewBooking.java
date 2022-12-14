package booking_bot.commands.impls;

import booking_bot.commands.CommandContainer;
import booking_bot.commands.CommandNames;
import booking_bot.commands.CommandParent;
import booking_bot.models.*;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewBooking extends CommandParent {
    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    private LocalDate selectedDate;
    private LocalTime timeStart;
    private BookObject bookObject;
    private final Booking booking;
    private User user;

    public NewBooking(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.ADMIN.getText();
        commandContainer.add(commandName, this);
        statusMap = new HashMap<>();

        booking = new Booking();
    }

    public boolean execute(Update update, boolean begin) {
        prepare(update);

        statusMap.putIfAbsent(chatId, Status.BEGIN);
        if (begin) {
            statusMap.put(chatId, currentStatus);
        }

        currentStatus = statusMap.get(chatId);
        user = controller.getUser().findByTelegram(chatId);

        if (currentStatus.equals(Status.BEGIN)) {
            isFinished = false;
            begin();
        } else if (currentStatus.equals(Status.SELECT_OBJECT)) {
            selectObject();
        } else if (this.currentStatus.equals(Status.SELECT_DATE)) {
            selectDate();
        } else if (currentStatus.equals(Status.SELECT_TIME)) {
            selectTime();
        } else if (currentStatus.equals(Status.ENTER_TIME_START)) {
            enterTimeStart();
        } else if (currentStatus.equals(Status.ENTER_TIME_END)) {
            enterTimeEnd();
        }

        return isFinished;
    }

    private void begin() {
        List<String> buttons = getNames(controller.getType().findAll());
        botService.sendMessage(chatId, "???????????????? ??????????????????", buttons);
        statusMap.put(chatId, Status.SELECT_OBJECT);
    }

    private void selectObject() {
        List<BookObject> bookObjectList = controller.getBookingObject().findByType(input);

        if (bookObjectList.isEmpty()) {
            botService.sendMessage(chatId, "???????????? ???? ??????????????", null);
            statusMap.put(chatId, Status.BEGIN);

        } else {
            botService.sendMessage(chatId, "???????????????? ?????? ??????????????????????????:", null);
            for (BookObject object : bookObjectList) {
                if (object.getImage() == null || object.getImage().equals("null") || object.getImage().equals("")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setParseMode("markdown");
                    if (object.getDescription().equals("null")) {
                        sendMessage.setText("*" + object.getName() + "*");
                    } else {
                        sendMessage.setText("*" + object.getName() + "*\n" + object.getDescription());
                    }
                    sendMessage.setReplyMarkup(makeBookButton(object.getName()));
                    botService.sendCustom(sendMessage);
                } else {
                    SendPhoto sendPhoto = makeObjectMessage(object);
                    botService.sendPhoto(sendPhoto);
                }
            }

            statusMap.put(chatId, Status.SELECT_DATE);
        }
    }

    private void selectDate() {
        bookObject = controller.getBookingObject().findByName(input);
        SendMessage send = new SendMessage();

        send.setChatId(chatId.toString());
        send.setText("???????????????? ???????? ????????????????????????:");
        send.setReplyMarkup(makeCalendar());
        botService.sendCustom(send);
        statusMap.put(chatId, Status.SELECT_TIME);
    }

    private void selectTime() {
        if (input.equals("x")) {
            botService.sendMessage(chatId, "???? ???????? ???????? ???? ??????????????????, ???????????????? ????????????", null);
        } else {
            selectedDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), Integer.parseInt(input));
            String bookedSlots = getBookedSlots(selectedDate);

            if (bookedSlots == null) {
                botService.sendMessage(chatId, "???? ?????? ???????? ?????? ????????????????", null);
            } else {
                botService.sendMessage(chatId, "???? ?????? ???????? ?????? ???????? ?????????? ????????????????????????:\n" + bookedSlots, null);
            }

            botService.sendMessage(chatId, "?????????????? ?????????? ???????????? ?? ?????????????? ????.????\n???????????????? 12.30", null);
            statusMap.put(chatId, Status.ENTER_TIME_START);
        }
    }

    private void enterTimeStart() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");

        try {
            timeStart = LocalTime.parse(input, timeFormatter);
            if (selectedDate.isEqual(LocalDate.now()) && timeStart.isBefore(LocalTime.now())) {
                botService.sendMessage(chatId, "?????????? ???????????? ???????? ???? ???????????? ????????????????.\n?????????????? ?????????? ???????????? ??????????", null);
                statusMap.put(chatId, Status.ENTER_TIME_START);
            } else {
                botService.sendMessage(chatId, "?????????????? ?????????? ??????????????????, ???????? ?? ?????????????? ????.????", null);
                statusMap.put(chatId, Status.ENTER_TIME_END);
            }
        } catch (DateTimeParseException var8) {
            System.err.println(var8.getMessage());
            botService.sendMessage(chatId, "???? ????????????????????, ?????????????????? ?????? ???????????? ?????????????? ????????????????????: ????.????\n???????????????? 12.30", null);
        }
    }

    private void enterTimeEnd() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");

        try {
            LocalTime timeEnd = LocalTime.parse(input, timeFormatter);

            if (timeEnd.isBefore(timeStart)) {
                botService.sendMessage(chatId, "?????????? ?????????????????? ???? ???????????? ???????? ???????????? ?????????????? ????????????.\n?????????????? ?????????? ?????????????????? ??????????", null);
                statusMap.put(chatId, Status.ENTER_TIME_END);
            } else if (!checkSlot(timeStart, timeEnd)) {
                botService.sendMessage(chatId, "???????? ?????????? ???????????????????????? ?? ?????? ?????????????????? ????????????.\n?????????????? ?????????? ???????????? ??????????", null);
                statusMap.put(chatId, Status.ENTER_TIME_START);
            } else {

                booking.setBookObject(bookObject);
                booking.setTimeStart(LocalDateTime.of(selectedDate, timeStart));
                booking.setTimeEnd(LocalDateTime.of(selectedDate, timeEnd));
                booking.setStatus(controller.getStatus().findByName("??????????"));

                booking.setUser(user);

                try {
                    controller.getBooking().save(booking);
                    botService.sendMessage(chatId, "???????????? \uD83D\uDC4D \n?????????????????????????? ???? "
                            + selectedDate.format(dateFormatter) + " ?? " + timeStart + " ???? " + timeEnd
                            + ":\n" + bookObject.getName(), null);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                    botService.sendMessage(chatId, "???? ????????????????????, ???????????? ?? ???????? ???????????? :(", null);
                }

                statusMap.put(chatId, Status.BEGIN);
                isFinished = true;
            }

        } catch (DateTimeParseException var7) {
            System.err.println(var7.getMessage());
            botService.sendMessage(chatId, "???? ????????????????????, ?????????????????? ?????? ???????????? ?????????????? ????????????????????: ????.????\n???????????????? 12.30", null);
        }
    }

    private InlineKeyboardMarkup makeCalendar() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate counter = today.minusDays(today.getDayOfWeek().getValue() - 1);

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

        if (bookingList == null || bookingList.isEmpty()) {
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
        InlineKeyboardButton button = new InlineKeyboardButton("??????????????????????????");
        button.setCallbackData(callBackText);
        keyboardButtonRow.add(button);
        totalList.add(keyboardButtonRow);
        inlineKeyboardMarkup.setKeyboard(totalList);

        return inlineKeyboardMarkup;
    }

    private enum Status {
        BEGIN,
        SELECT_OBJECT,
        SELECT_DATE,
        SELECT_TIME,
        ENTER_TIME_START,
        ENTER_TIME_END
    }

    public String getCommandName() {
        return commandName;
    }

}
