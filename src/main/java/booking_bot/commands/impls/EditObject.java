package booking_bot.commands.impls;

import booking_bot.commands.CommandContainer;
import booking_bot.commands.CommandNames;
import booking_bot.commands.CommandParent;
import booking_bot.models.BookObject;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *   Поля унаследованные от родительского класса:
 *
 *   Long chatId - ID чата с пользователем
 *   String input - сообщение от пользователя
 *   String status - текущий статус, в начале он всегда "begin", а дальше надо перезаписывать на каждом шаге
 *   boolean isFinished - обозначает что весь сценарий прошли, в начале false, в самом конце меняем на true
 */
public class EditObject extends CommandParent {

    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    private BookObject tmpObject;

    public EditObject(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.EDIT_OBJECT.getText();
        commandContainer.add(commandName, this);
        statusMap = new HashMap<>();
    }

    @Override
    public boolean execute(Update update, boolean begin) {

        prepare(update);

        statusMap.putIfAbsent(chatId, Status.BEGIN);
        if (begin) {
            statusMap.put(chatId, currentStatus);
        }

        currentStatus = statusMap.get(chatId);

        if (currentStatus.equals(Status.BEGIN)) {
            isFinished = false;
            begin();
        } else if (currentStatus.equals(Status.SELECT_CATEGORY)) {
            selectCategory();
        } else if (currentStatus.equals(Status.SELECT_OBJECT)) {
            selectObject();
        } else if (currentStatus.equals(Status.SELECT_ACTION)) {
            selectAction();
        } else if (currentStatus.equals(Status.CHANGE_NAME)) {
            changeName();
        } else if (currentStatus.equals(Status.CHANGE_DESCRIPTION)) {
            changeCategory();
        } else if (currentStatus.equals(Status.DELETE)) {
            delete();
        }

        return isFinished;
    }

    private void begin() {
        List<String> buttons = getNames(controller.getType().findAll());
        botService.sendMessage(chatId, "Выберите категорию", buttons);
        statusMap.put(chatId, Status.SELECT_CATEGORY);
    }

    private void selectCategory() {
        List<String> objects = getNames(controller.getBookingObject().findByType(input));
        botService.sendMessage(chatId, "Выберите объект", objects);
        statusMap.put(chatId, Status.SELECT_OBJECT);
    }

    private void selectObject() {
        tmpObject = controller.getBookingObject().findByName(input);
        List<String> actions = new ArrayList<>();
        actions.add("Изменить название");
        actions.add("Изменить описание");
        actions.add("Удалить");
        botService.sendMessage(chatId, "Выберите действие", actions);
        statusMap.put(chatId, Status.SELECT_ACTION);
    }

    private void selectAction() {
        switch (input) {
            case "Изменить название":
                botService.sendMessage(chatId, "Введите новое название", null);
                statusMap.put(chatId, Status.CHANGE_NAME);
                break;
            case "Изменить описание":
                botService.sendMessage(chatId, "Введите новое описание", null);
                statusMap.put(chatId, Status.CHANGE_DESCRIPTION);
                break;
            case "Удалить":
                botService.sendMessage(chatId, "Вы удалили объект " + tmpObject.getName(), null);
                controller.getBookingObject().delete(tmpObject);
                statusMap.put(chatId, Status.DELETE);
                break;
        }
    }

    private void changeName() {
        tmpObject.setName(input);
        controller.getBookingObject().update(tmpObject);
        botService.sendMessage(chatId, "Вы изменили на " + input, null);
        statusMap.put(chatId, Status.BEGIN);
    }

    private void changeCategory() {
        tmpObject.setDescription(input);
        controller.getBookingObject().update(tmpObject);
        botService.sendMessage(chatId, "Вы изменили описание объекта " + tmpObject.getName(), null);
        statusMap.put(chatId, Status.BEGIN);
    }

    private void delete() {
        botService.sendMessage(chatId, "Объект " + tmpObject.getName() + " удален", null);
        controller.getBookingObject().delete(tmpObject);
        statusMap.put(chatId, Status.BEGIN);
    }

    private enum Status {
        BEGIN,
        SELECT_CATEGORY,
        SELECT_OBJECT,
        SELECT_ACTION,
        CHANGE_NAME,
        CHANGE_DESCRIPTION,
        DELETE
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

}
