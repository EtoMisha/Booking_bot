package ru.booking.bot.command.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.format.DateTimeParseException;
import java.util.*;

@RequiredArgsConstructor
@Component
@Scope("prototype")
public class EditObject implements Command {

    private enum Step {
        BEGIN,
        SELECT_CATEGORY,
        SELECT_OBJECT,
        SELECT_ACTION,
        CHANGE_NAME,
        CHANGE_DESCRIPTION,
        CHANGE_AVAILABILITY_FROM,
        CHANGE_AVAILABILITY_TO,
        CHANGE_CATEGORY,
        CHANGE_IMAGE,
        DELETE
    }

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");

    private static final String CANCEL_VALUE = "-1";
    private static final String SELECT_CATEGORY = "Выберите категорию";
    private static final String SELECT_OBJECT = "Выберите кого изменить";
    private static final String CHANGE_NAME = "Изменить название";
    private static final String CHANGE_DESCRIPTION = "Изменить описание";
    private static final String CHANGE_AVAILABILITY = "Изменить интервал доступности";
    private static final String CHANGE_CATEGORY = "Изменить категорию";
    private static final String CHANGE_IMAGE = "Изменить изображение";
    private static final String DELETE = "Удалить";
    private static final String CANCEL = "Отмена";
    private static final String SELECT_ACTION = "Выберите действие";
    private static final String ENTER_NEW_NAME = "Введите новое название";
    private static final String ENTER_NEW_DESCRIPTION = "Введите новое описание";
    private static final String ENTER_AVAILABLE_FROM = "Введите время с которого будет доступно бронирование в формате ЧЧ.ММ, например 10.00";
    private static final String ENTER_AVAILABLE_TO = "Введите до какого времени будет доступно бронирование в формате ЧЧ.ММ, например 19.00";
    private static final String TOO_EARLY_TIME = "Время должно быть не раньше времени начала.\nВведите время окончания снова";
    private static final String INCORRECT_TIME_FORMAT = "Не получилось, проверьте что формат времени правильный: ЧЧ.ММ\nНапример 12.30";
    private static final String SELECT_NEW_CATEGORY = "Выберите новую категорию";
    private static final String UPLOAD_NEW_IMAGE = "Загрузите новое изображение";
    private static final String CONFIRM_DELETING = "Вы уверены что хотите удалить %s?";
    private static final String DONE = "Готово";
    private static final String UNABLE_TO_UPLOAD = "Не получилось сохранить изображение, попробуйте другое";

    private final BotService botService;
    private final TypeService typeService;
    private final BookingObjectService bookingObjectService;

    private final Map<Long, Step> usersSteps = new HashMap<>();
    private boolean isFinished;
    private BookingObject bookingObject;
    
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
            case SELECT_OBJECT -> selectObject(userId, input);
            case SELECT_ACTION -> selectAction(userId, input);
            case CHANGE_NAME -> changeName(userId, input);
            case CHANGE_DESCRIPTION -> changeDescription(userId, input);
            case CHANGE_AVAILABILITY_FROM -> changeAvailabilityFrom(userId, input);
            case CHANGE_AVAILABILITY_TO -> changeAvailabilityTo(userId, input);
            case CHANGE_CATEGORY -> changeCategory(userId, input);
            case CHANGE_IMAGE -> changeImage(update);
            case DELETE -> delete(userId);
        }

        return isFinished;
    }

    @Override
    public CommandName getName() {
        return CommandName.EDIT_OBJECT;
    }

    private void begin(long userId) {
        botService.sendWithKeyboard(userId, SELECT_CATEGORY, getTypesButtons());
        usersSteps.put(userId, Step.SELECT_CATEGORY);
    }

    private void selectCategory(long userId, String input) {
        botService.sendWithKeyboard(userId, SELECT_OBJECT, getObjectsButtons(input));
        usersSteps.put(userId, Step.SELECT_OBJECT);
    }

    private void selectObject(long userId, String input) {
        Optional<BookingObject> objectOpt = bookingObjectService.findById(input);
        if (objectOpt.isPresent()) {
            bookingObject = objectOpt.get();
            botService.sendWithKeyboard(userId, SELECT_ACTION, getActionsButtons());
            usersSteps.put(userId, Step.SELECT_ACTION);
        }
    }

    private void selectAction(long userId, String input) {
        try {
            Step step = Step.valueOf(input);
            switch (step) {
                case CHANGE_NAME -> botService.sendText(userId, ENTER_NEW_NAME);
                case CHANGE_DESCRIPTION -> botService.sendText(userId, ENTER_NEW_DESCRIPTION);
                case CHANGE_AVAILABILITY_FROM -> botService.sendText(userId, ENTER_AVAILABLE_FROM);
                case CHANGE_CATEGORY -> botService.sendWithKeyboard(userId, SELECT_NEW_CATEGORY, getTypesButtons());
                case CHANGE_IMAGE -> botService.sendText(userId, UPLOAD_NEW_IMAGE);
                case DELETE -> botService.sendWithKeyboard(userId,
                        CONFIRM_DELETING.formatted(bookingObject.getName()), getConfirmButtons());
            }

            usersSteps.put(userId, step);
        } catch (IllegalArgumentException ignored) {}
    }

    private void changeName(long userId, String input) {
        bookingObject.setName(input);
        confirmChanges(userId);
    }

    private void changeDescription(long userId, String input) {
        bookingObject.setDescription(input);
        confirmChanges(userId);
    }

    private void changeAvailabilityFrom(long userId, String input) {
        try {
            LocalTime availability = LocalTime.parse(input, timeFormatter);
            bookingObject.setAvailableFrom(availability);

            botService.sendText(userId, ENTER_AVAILABLE_TO);
            usersSteps.put(userId, Step.CHANGE_AVAILABILITY_TO);
        } catch (DateTimeParseException e) {
            botService.sendText(userId, INCORRECT_TIME_FORMAT);
        }
    }

    private void changeAvailabilityTo(long userId, String input) {
        try {
            LocalTime availability = LocalTime.parse(input, timeFormatter);
            if (availability.isBefore(bookingObject.getAvailableFrom())) {
                botService.sendText(userId, TOO_EARLY_TIME);
            } else {
                bookingObject.setAvailableTo(availability);
                confirmChanges(userId);
            }
        } catch (DateTimeParseException e) {
            botService.sendText(userId, INCORRECT_TIME_FORMAT);
        }
    }

    private void changeCategory(long userId, String input) {
        Optional<Type> typeOpt = typeService.findById(input);
        typeOpt.ifPresent(type -> bookingObject.setType(type));
        confirmChanges(userId);
    }

    private void changeImage(Update update) {
        Long userId = UpdateUtil.getUserId(update);
        if (update.getMessage().hasPhoto()) {
            try {
                String filePath = botService.downloadPhoto(update);
                bookingObject.setImage(filePath);
                confirmChanges(userId);
            } catch (TelegramApiException e) {
                botService.sendText(userId, UNABLE_TO_UPLOAD);
                e.printStackTrace();
            }
        } else {
            botService.sendText(userId, UPLOAD_NEW_IMAGE);
        }
    }

    private void delete(long userId) {
        bookingObjectService.delete(bookingObject);
        botService.sendText(userId, DONE);
        usersSteps.put(userId, Step.BEGIN);
        isFinished = true;
    }

    private void confirmChanges(long userId) {
        bookingObjectService.save(bookingObject);
        botService.sendText(userId, DONE);
        usersSteps.put(userId, Step.BEGIN);
        isFinished = true;
    }

    private List<Button> getTypesButtons() {
        List<Button> typesButtons = new ArrayList<>();
        List<Type> typesList = typeService.findAll();
        typesList.forEach(type -> typesButtons.add(new Button(String.valueOf(type.getId()), type.getName())));
        return typesButtons;
    }

    private List<Button> getObjectsButtons(String typeId) {
        List<Button> typesButtons = new ArrayList<>();
        List<BookingObject> objectsList = bookingObjectService.findByTypeId(typeId);
        objectsList.forEach(type -> typesButtons.add(new Button(String.valueOf(type.getId()), type.getName())));
        return typesButtons;
    }

    private List<Button> getActionsButtons() {
        return List.of(
                new Button(Step.CHANGE_NAME.toString(), CHANGE_NAME),
                new Button(Step.CHANGE_NAME.toString(), CHANGE_NAME),
                new Button(Step.CHANGE_DESCRIPTION.toString(), CHANGE_DESCRIPTION),
                new Button(Step.CHANGE_AVAILABILITY_FROM.toString(), CHANGE_AVAILABILITY),
                new Button(Step.CHANGE_CATEGORY.toString(), CHANGE_CATEGORY),
                new Button(Step.CHANGE_IMAGE.toString(), CHANGE_IMAGE),
                new Button(Step.DELETE.toString(), DELETE)
        );
    }

    private List<Button> getConfirmButtons() {
        return List.of(
                new Button(String.valueOf(bookingObject.getId()), DELETE),
                new Button(CANCEL_VALUE, CANCEL)
        );
    }

}
