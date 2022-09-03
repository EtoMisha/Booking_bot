package booking_bot.commands.impls;

import booking_bot.commands.CommandContainer;
import booking_bot.commands.CommandNames;
import booking_bot.commands.CommandParent;
import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.springframework.dao.DataAccessException;
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
public class EditUser extends CommandParent {

    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    private User newUser;
    private boolean needToEdit;

    public EditUser(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.EDIT_OBJECT.getText();
        commandContainer.add(commandName, this);
        statusMap = new HashMap<>();

        newUser = new User();
        needToEdit = false;
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
        } else if (currentStatus.equals(Status.ENTER_LOGIN)) {
            enterLogin();
        } else if (currentStatus.equals(Status.EDIT)) {
            edit();
        } else if (currentStatus.equals(Status.SELECT_FIELD)) {
            selectField();
        } else if (currentStatus.equals(Status.ADD)) {
            add();
        } else if (currentStatus.equals(Status.ENTER_NAME)) {
            enterName();
        } else if (currentStatus.equals(Status.EDIT_CAMPUS)) {
            editCampus();
        } else if (currentStatus.equals(Status.EDIT_ROLE)) {
            editRole();
        }

        return isFinished;
    }

    private void begin() {
        botService.sendMessage(chatId, "Введите логин", null);
        statusMap.put(chatId, Status.ENTER_LOGIN);
    }

    private void enterLogin() {
        newUser = controller.getUser().findByName(input);

        if (newUser != null) {
            List<String> editButtons = new ArrayList<>();
            editButtons.add("Редактировать");
            editButtons.add("Удалить");

            botService.sendMessage(chatId, "Пользователь с ником " + newUser.getLogin() +
                    " найден. Вы можете удалить или отредактировать пользователя.", editButtons);
            statusMap.put(chatId, Status.EDIT);
        } else {
            List<String> addButtons = new ArrayList<>();
            addButtons.add("Добавить");

            botService.sendMessage(chatId, "Пользователь c ником " + input +
                    " не существует. Вы можете добавить пользователя.", addButtons);
            statusMap.put(chatId, Status.ADD);
        }
    }

    private void edit() {
        if (input.equals("Редактировать")) {
            List<String> buttons = new ArrayList<>();
            buttons.add("Роль");
            buttons.add("Кампус");

            botService.sendMessage(chatId, "Что будем редактировать?", buttons);
            statusMap.put(chatId, Status.SELECT_FIELD);
        }

        if (input.equals("Удалить")) {
            botService.sendMessage(chatId, "Пользователь " + newUser.getLogin() + " удален", null);
            controller.getUser().delete(newUser);
            statusMap.put(chatId, Status.BEGIN);
            isFinished = true;
        }
    }

    private void add() {
        botService.sendMessage(chatId, "Введите имя пользователя", null);
        statusMap.put(chatId, Status.ENTER_NAME);
    }

    private void selectField() {
        if (input.equals("Роль")) {
            needToEdit = true;
            botService.sendMessage(chatId, "Выберите роль", getNames(controller.getRoles().findAll()));
            statusMap.put(chatId, Status.EDIT_ROLE);
        }

        if (input.equals("Кампус")) {
            needToEdit = true;
            botService.sendMessage(chatId, "Выберите кампус", getNames(controller.getCampus().findAll()));
            statusMap.put(chatId, Status.EDIT_CAMPUS);
        }
    }

    private void enterName() {
        newUser.setName(input);
        botService.sendMessage(chatId, "Ок, а теперь выберите кампус", getNames(controller.getCampus().findAll()));
        statusMap.put(chatId, Status.EDIT_CAMPUS);
    }

    private void editRole() {
        newUser.setRole(controller.getRole().findByName(input));

        if (needToEdit) {
            botService.sendMessage(chatId, "Пользователь отредактирован" +'\n'
                    + "логин: " + newUser.getLogin() + '\n' + "имя: " + newUser.getName() + '\n'
                    + "кампус: " + newUser.getCampus() + '\n' + "роль: " + newUser.getRole(), null);
            controller.getUser().update(newUser);
            needToEdit = false;
        } else {
            try {
                newUser.setTelegramId(chatId);
                controller.getUser().save(newUser);
                botService.sendMessage(chatId, "Новый пользователь добавлен:"
                        + '\n' + "логин: " + newUser.getLogin() + '\n'
                        + "имя: " + newUser.getName() + '\n' + "кампус: " + newUser.getCampus() + '\n'
                        + "роль: " + newUser.getRole(), null);
            } catch (DataAccessException e) {
                botService.sendMessage(chatId, "Не получилось сохранить", null);
            }
        }

        statusMap.put(chatId, Status.BEGIN);
        isFinished = true;
    }

    private void editCampus() {
        newUser.setCampus(controller.getCampus().findByName(input));

        if (needToEdit){
            botService.sendMessage(chatId, "Пользователь отредактирован" +'\n'
                    + "логин: " + newUser.getLogin() + '\n' + "имя: " + newUser.getName() + '\n'
                    + "кампус: " + newUser.getCampus() + '\n' + "роль: " + newUser.getRole(), null);
            controller.getUser().update(newUser);
            statusMap.put(chatId, Status.BEGIN);
            needToEdit = false;
            isFinished = true;
        } else {
            botService.sendMessage(chatId, "Осталось выбрать роль", getNames(controller.getRoles().findAll()));
            statusMap.put(chatId, Status.EDIT_ROLE);
        }
    }

    private enum Status {
        BEGIN,
        ENTER_LOGIN,
        EDIT,
        ADD,
        SELECT_FIELD,
        ENTER_NAME,
        EDIT_ROLE,
        EDIT_CAMPUS
    }

    @Override
    public String getCommandName () {
        return commandName;
    }
}
