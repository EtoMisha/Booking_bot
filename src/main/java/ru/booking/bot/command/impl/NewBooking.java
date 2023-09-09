package ru.booking.bot.command.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.booking.bot.bot.Button;
import ru.booking.bot.bot.UpdateUtil;
import ru.booking.bot.command.Command;
import ru.booking.bot.command.CommandName;
import ru.booking.bot.models.Booking;
import ru.booking.bot.bot.BotService;
import ru.booking.bot.models.BookingObject;
import ru.booking.bot.models.User;
import ru.booking.bot.service.BookingObjectService;
import ru.booking.bot.service.BookingService;
import ru.booking.bot.service.TypeService;
import ru.booking.bot.service.UserService;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@RequiredArgsConstructor
@Component
@Scope("prototype")
public class NewBooking implements Command {

    private enum Step {
        BEGIN,
        SELECT_OBJECT,
        SELECT_DATE,
        ENTER_TIME_START,
        ENTER_TIME_END,
        CONFIRMATION
    }

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");

    private static final String SELECT_CATEGORY = "Выберите категорию";
    private static final String NOTHING_FOUND = "Ничего не нашлось";
    private static final String SELECT_OBJECT = "Выберите что забронировать:";
    private static final String BOOK = "Забронировать";
    private static final String SELECT_DATE = "Выберите дату:";
    private static final String INCORRECT_DAY = "На этот день не получится, выберите другой";
    private static final String AVAILABLE_SLOTS = "Доступное время:";
    private static final String ENTER_TIME_START = "Введите время начала в формате ЧЧ.ММ\nНапример 12.30";
    private static final String TOO_EARLY_TIME_START = "Время должно быть не раньше текущего.\nВведите время начала снова";
    private static final String ENTER_TIME_END = "Введите время окончания, тоже в формате ЧЧ.ММ";
    private static final String INCORRECT_TIME_FORMAT = "Не получилось, проверьте что формат времени правильный: ЧЧ.ММ\nНапример 12.30";
    private static final String TOO_EARLY_TIME_END = "Время окончания не должно быть раньше времени начала.\nВведите время окончания снова";
    private static final String TIME_INTERSECT = "Ваше время пересекается с уже имеющимся слотом.\nВведите время начала снова";
    private static final String CONFIRMATION_TEXT = "Готово!\nЗабронировали на %s\nС %s до %s\n%s";

    private final BotService botService;
    private final TypeService typeService;
    private final BookingObjectService bookingObjectService;
    private final BookingService bookingService;
    private final UserService userService;

    private final Map<Long, Step> usersSteps = new HashMap<>();
    private boolean isFinished;
    private Booking booking;

    @Override
    public boolean execute(Update update, boolean isBeginning) {
        Long userId = UpdateUtil.getUserId(update);
        String input = UpdateUtil.getInput(update);

        if (!usersSteps.containsKey(userId) || isBeginning) {
            usersSteps.put(userId, Step.BEGIN);
        }

        switch (usersSteps.get(userId)) {
            case BEGIN -> begin(update);
            case SELECT_OBJECT -> selectObject(userId, input);
            case SELECT_DATE -> selectDate(userId, input);
            case ENTER_TIME_START -> enterTimeStart(userId, input);
            case ENTER_TIME_END -> enterTimeEnd(userId, input);
            case CONFIRMATION -> confirmation(userId, input);
        }

        return isFinished;
    }

    @Override
    public CommandName getName() {
        return CommandName.NEW_BOOKING;
    }

    private void begin(Update update) {
        long userId = UpdateUtil.getUserId(update);
        List<Button> buttons = new ArrayList<>();

        Optional<User> userOpt = userService.findById(userId);
        booking = new Booking();
        booking.setUser(userOpt.orElseGet(() -> userService.create(update)));

        typeService.findAll().forEach(type -> buttons.add(new Button(String.valueOf(type.getId()), type.getName())));
        botService.sendWithKeyboard(userId, SELECT_CATEGORY, buttons);
        usersSteps.put(userId, Step.SELECT_OBJECT);
    }

    private void selectObject(Long userId, String input) {
        List<BookingObject> objectList = bookingObjectService.findByTypeId(input);

        if (objectList.isEmpty()) {
            botService.sendText(userId, NOTHING_FOUND);
            usersSteps.put(userId, Step.BEGIN);
        } else {
            botService.sendText(userId, SELECT_OBJECT);

            for (BookingObject object : objectList) {
                List<Button> buttons = List.of(new Button(String.valueOf(object.getId()), BOOK));
                if (object.getImage() == null || object.getImage().equals("null") || object.getImage().equals("")) {
                    botService.sendWithKeyboard(userId, object.getFullText(), buttons);
                } else {
                    botService.sendPhoto(getPhotoMessage(userId, object), buttons);
                }
            }

            usersSteps.put(userId, Step.SELECT_DATE);
        }
    }

    private void selectDate(Long userId, String input) {
        Optional<BookingObject> objectOpt = bookingObjectService.findById(input);
        if (objectOpt.isPresent()) {
            booking.setBookingObject(objectOpt.get());

            botService.sendMarkup(userId, SELECT_DATE, makeCalendar());
            usersSteps.put(userId, Step.ENTER_TIME_START);
        }
    }

    private void enterTimeStart(Long userId, String input) {
        LocalDate selectedDate = LocalDate.parse(input, dateFormatter);
        if (selectedDate.isBefore(LocalDate.now())) {
            botService.sendText(userId, INCORRECT_DAY);
        } else {
            booking.setTimeStart(selectedDate.atStartOfDay());

            botService.sendText(userId, AVAILABLE_SLOTS + "\n"
                    + getAvailableSlots(booking.getBookingObject(), selectedDate));
            botService.sendText(userId, ENTER_TIME_START);
            usersSteps.put(userId, Step.ENTER_TIME_END);
        }
    }

    private void enterTimeEnd(Long userId, String input) {
        try {
            LocalTime timeStart = LocalTime.parse(input, timeFormatter);
            if (booking.getTimeStart().toLocalDate().atTime(timeStart).isBefore(LocalDateTime.now())) {
                botService.sendText(userId, TOO_EARLY_TIME_START);
            } else {
                booking.setTimeStart(booking.getTimeStart().toLocalDate().atTime(timeStart));

                botService.sendText(userId, ENTER_TIME_END);
                usersSteps.put(userId, Step.CONFIRMATION);
            }
        } catch (DateTimeParseException e) {
            botService.sendText(userId, INCORRECT_TIME_FORMAT);
        }
    }

    private void confirmation(Long userId, String input) {
        try {
            LocalTime timeEnd = LocalTime.parse(input, timeFormatter);
            LocalTime timeStart = booking.getTimeStart().toLocalTime();
            if (timeEnd.isBefore(timeStart)) {
                botService.sendText(userId, TOO_EARLY_TIME_END);
            } else if (!checkSlot(timeStart, timeEnd)) {
                botService.sendText(userId, TIME_INTERSECT);
                usersSteps.put(userId, Step.ENTER_TIME_END);
            } else {
                booking.setTimeEnd(LocalDateTime.of(booking.getTimeStart().toLocalDate(), timeEnd));
                bookingService.save(booking);

                String confirmationText = String.format(CONFIRMATION_TEXT,
                        booking.getTimeStart().format(dateFormatter), timeStart, timeEnd,
                        booking.getBookingObject().getName());
                botService.sendText(userId, confirmationText);

                usersSteps.put(userId, Step.BEGIN);
                isFinished = true;
            }

        } catch (DateTimeParseException e) {
            botService.sendText(userId, INCORRECT_TIME_FORMAT);
        }
    }

    private InlineKeyboardMarkup makeCalendar() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate day = today.minusDays(today.getDayOfWeek().getValue() - 1);

        for(int calendarRow = 0; calendarRow < 4; calendarRow++) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
            for(int calendarColumn = 0; calendarColumn < 7; calendarColumn++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(day.isBefore(today) ? "X" : String.valueOf(day.getDayOfMonth()));
                button.setCallbackData(day.format(dateFormatter));
                keyboardButtonRow.add(button);
                day = day.plusDays(1);
            }
            totalList.add(keyboardButtonRow);
        }
        inlineKeyboardMarkup.setKeyboard(totalList);
        return inlineKeyboardMarkup;
    }

    private String getAvailableSlots(BookingObject object, LocalDate date) {
        List<Booking> bookingList = bookingService.findByDateAndObject(date, booking.getBookingObject());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(object.getAvailableFrom()).append(" - ");
        for (Booking booking: bookingList) {
            stringBuilder.append(booking.getTimeStart().toLocalTime())
                    .append("\n")
                    .append(booking.getTimeEnd().toLocalTime())
                    .append(" - ");
        }
        stringBuilder.append(object.getAvailableTo());

        return stringBuilder.toString();
    }

    private boolean checkSlot(LocalTime timeStart, LocalTime timeEnd) {
        List<Booking> bookingList = bookingService.findByDateAndObject(booking.getTimeStart().toLocalDate(),
                booking.getBookingObject());
        for (Booking booking : bookingList) {
            LocalTime start = booking.getTimeStart().toLocalTime();
            LocalTime end = booking.getTimeEnd().toLocalTime();
            if (timeStart.isBefore(end) || timeEnd.isBefore(start)) {
                return false;
            }
        }

        return true;
    }

    private SendPhoto getPhotoMessage(long userId, BookingObject object) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(userId));
        sendPhoto.setParseMode(ParseMode.MARKDOWN);
        sendPhoto.setCaption(object.getFullText());
        sendPhoto.setPhoto(new InputFile(new File(object.getImage())));

        return sendPhoto;
    }

}
