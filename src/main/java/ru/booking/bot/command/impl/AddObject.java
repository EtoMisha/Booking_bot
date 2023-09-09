package ru.booking.bot.command.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.booking.bot.bot.BotService;
import ru.booking.bot.bot.Button;
import ru.booking.bot.bot.UpdateUtil;
import ru.booking.bot.command.Command;
import ru.booking.bot.command.CommandName;
import ru.booking.bot.models.BookingObject;
import ru.booking.bot.models.Type;
import ru.booking.bot.service.BookingObjectService;
import ru.booking.bot.service.TypeService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Component
@Scope("prototype")
public class AddObject implements Command {

    private enum Step {
        BEGIN,
        SELECT_CATEGORY,
        ENTER_CATEGORY_NAME,
        ENTER_NAME,
        ENTER_DESCRIPTION,
        ENTER_AVAILABILITY_START,
        ENTER_AVAILABILITY_END,
        UPLOAD_IMAGE
    }

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");

    private static final String EMPTY = "0";
    private static final String SELECT_CATEGORY = "Выберите категорию";
    private static final String CREATE_TYPE = "Добавить категорию";
    private static final String ENTER_NAME = "Введите название";
    private static final String ENTER_CATEGORY_NAME = "Введите название категории";
    private static final String ENTER_DESCRIPTION = "Введите описание";
    private static final String ALREADY_EXIST = "Такой уже есть, введите другое название";
    private static final String ENTER_AVAILABILITY_START = "Введите время с которого будет доступно бронирование в формате ЧЧ.ММ, например 10.00";
    private static final String ENTER_AVAILABILITY_END = "Введите до какого времени будет доступно бронирование в формате ЧЧ.ММ, например 19.00";
    private static final String UPLOAD_IMAGE = "Загрузите изображение";
    private static final String SKIP = "Пропустить";
    private static final String DONE = "Готово";
    private static final String CONFIRMATION = "Готово:\n*%s*\n%s";
    private static final String UNABLE_TO_UPLOAD = "Не получилось сохранить изображение, попробуйте другое";

    private final BotService botService;
    private final TypeService typeService;
    private final BookingObjectService bookingObjectService;

    private final Map<Long, Step> usersSteps = new HashMap<>();
    private boolean isFinished;
    private BookingObject bookingObject;

    @Value("${bot.default-start-time}")
    private String defaultAvailabilityStart;

    @Value("${bot.default-end-time}")
    private String defaultAvailabilityEnd;

    @Override
    public boolean execute(Update update, boolean isBeginning) {
        long userId = UpdateUtil.getUserId(update);
        String input = UpdateUtil.getInput(update);

        if (!usersSteps.containsKey(userId) || isBeginning) {
            usersSteps.put(userId, Step.BEGIN);
        }

        switch (usersSteps.get(userId)) {
            case BEGIN -> begin(userId);
            case SELECT_CATEGORY -> selectCategory(userId, input);
            case ENTER_CATEGORY_NAME -> enterCategoryName(userId, input);
            case ENTER_NAME -> enterName(userId, input);
            case ENTER_DESCRIPTION -> enterDescription(userId, input);
            case ENTER_AVAILABILITY_START -> enterAvailabilityStart(userId, input);
            case ENTER_AVAILABILITY_END -> enterAvailabilityEnd(userId, input);
            case UPLOAD_IMAGE -> uploadOrSkipImage(update);
        }

        return isFinished;
    }

    @Override
    public CommandName getName() {
        return CommandName.ADD_OBJECT;
    }

    private void begin(long userId) {
        botService.sendWithKeyboard(userId, SELECT_CATEGORY, getTypesButtons());
        bookingObject = new BookingObject();
        usersSteps.put(userId, Step.SELECT_CATEGORY);
    }

    private void selectCategory(long userId, String input) {
        Optional<Type> typeOpt = typeService.findById(input);
        if (typeOpt.isPresent()) {
            bookingObject.setType(typeOpt.get());
            botService.sendText(userId, ENTER_NAME);
            usersSteps.put(userId, Step.ENTER_NAME);
        } else if (EMPTY.equals(input)) {
            botService.sendText(userId, ENTER_CATEGORY_NAME);
            usersSteps.put(userId, Step.ENTER_CATEGORY_NAME);
        }
    }

    private void enterCategoryName(long userId, String input) {
        Type type = new Type();
        type.setName(input);
        typeService.save(type);

        botService.sendText(userId, DONE);
        usersSteps.put(userId, Step.BEGIN);
    }

    private void enterName(long userId, String input) {
        List<BookingObject> objectList = bookingObjectService.findByName(input);
        if (objectList.isEmpty()) {
            bookingObject.setName(input);

            botService.sendWithKeyboard(userId, ENTER_DESCRIPTION, getSkipButton());
            usersSteps.put(userId, Step.ENTER_DESCRIPTION);
        } else {
            botService.sendText(userId, ALREADY_EXIST);
        }
    }

    private void enterDescription(long userId, String input) {
        String description = EMPTY.equals(input) ? "" : input;
        bookingObject.setDescription(description);

        botService.sendWithKeyboard(userId, ENTER_AVAILABILITY_START, getSkipButton());
        usersSteps.put(userId, Step.ENTER_AVAILABILITY_START);
    }

    private void enterAvailabilityStart(long userId, String input) {
        String availability = EMPTY.equals(input) ? defaultAvailabilityStart : input;
        bookingObject.setAvailableFrom(LocalTime.parse(availability, timeFormatter));

        botService.sendWithKeyboard(userId, ENTER_AVAILABILITY_END, getSkipButton());
        usersSteps.put(userId, Step.ENTER_AVAILABILITY_END);
    }

    private void enterAvailabilityEnd(long userId, String input) {
        String availability = EMPTY.equals(input) ? defaultAvailabilityEnd : input;
        bookingObject.setAvailableTo(LocalTime.parse(availability, timeFormatter));

        botService.sendWithKeyboard(userId, UPLOAD_IMAGE, getSkipButton());
        usersSteps.put(userId, Step.UPLOAD_IMAGE);
    }

    private void uploadOrSkipImage(Update update) {
        long userId = UpdateUtil.getUserId(update);
        String input = UpdateUtil.getInput(update);
        if (EMPTY.equals(input)) {
            bookingObjectService.save(bookingObject);
            botService.sendText(userId, getConfirmationText());
        } else if (update.getMessage().hasPhoto()) {
            try {
                String filePath = botService.downloadPhoto(update);
                bookingObject.setImage(filePath);
                bookingObjectService.save(bookingObject);
                botService.sendText(userId, getConfirmationText());

                usersSteps.put(userId, Step.BEGIN);
                isFinished = true;
            } catch (TelegramApiException e) {
                botService.sendText(userId, UNABLE_TO_UPLOAD);
                e.printStackTrace();
            }
        }
    }

    private List<Button> getTypesButtons() {
        List<Button> typesButtons = new ArrayList<>();
        List<Type> typesList = typeService.findAll();
        typesList.forEach(type -> typesButtons.add(new Button(String.valueOf(type.getId()), type.getName())));
        typesButtons.add(new Button(EMPTY, CREATE_TYPE));
        return typesButtons;
    }

    private String getConfirmationText() {
        return CONFIRMATION.formatted(bookingObject.getName(), bookingObject.getDescription());
    }

    private List<Button> getSkipButton() {
        return List.of(new Button(EMPTY, SKIP));
    }
}

