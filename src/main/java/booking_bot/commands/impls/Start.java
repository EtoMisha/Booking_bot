package booking_bot.commands.impls;

import booking_bot.commands.CommandContainer;
import booking_bot.commands.CommandNames;
import booking_bot.commands.CommandParent;
import booking_bot.commands.KeyboardMaker;
import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
public class Start extends CommandParent {

    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    private User newUser;

    public Start(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.START.getText();
        commandContainer.add(commandName, this);
        statusMap = new HashMap<>();

        newUser = new User();
    }

    public List<String> makeButtonsCampus() {
        return (getNames(controller.getCampus().findAll()));
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
        } else if (currentStatus.equals(Status.ENTER_NAME)) {
            enterName();
        } else if (currentStatus.equals(Status.SELECT_CAMPUS)) {
            selectCampus();
        }

        return isFinished;
    }

    private void begin() {
        try {
            newUser = controller.getUser().findByTelegram(chatId);
            SendMessage send = new SendMessage(chatId.toString(), "Привет, "+ newUser.getName());// + ". Что будем бронировать?");

            if (newUser.getRole().getName().equals("Студент")) {
                send.setReplyMarkup(KeyboardMaker.makeStudentKeyboard());
            }
            if (newUser.getRole().getName().equals("Администратор")){
                send.setReplyMarkup(KeyboardMaker.makeAdminKeyboard());
            }
            botService.sendCustom(send);

            statusMap.put(chatId, Status.BEGIN);

        } catch (DataAccessException ex) {
            botService.sendMessage(chatId, "Привет, это бот Школы 21 для бронирования помещений инвентаря и вообще всего.\n", null);
            botService.sendMessage(chatId, "Давай зарегистрируем тебя. Введи логин", null);
            statusMap.put(chatId, Status.ENTER_LOGIN);
        }
    }

    private void enterLogin() {
        boolean isUserExist = false;
        newUser.setLogin(input.toLowerCase());
        List<User> usersList = controller.getUser().findAll();

        for(User obj : usersList) {
            if (obj.getLogin().equals(input.toLowerCase())) {
                isUserExist = true;
                newUser = obj;
            }
        }

        if (isUserExist) {
            botService.sendMessage(chatId, "Пользователь " + newUser.getLogin() + " авторизован. Можешь бронировать.", null);
        } else {
            botService.sendMessage(chatId, "Введите имя пользователя", null);
            statusMap.put(chatId, Status.ENTER_NAME);
        }
    }

    private void enterName() {
        newUser.setName(input);
        botService.sendMessage(chatId, "Ок, а теперь выберите кампус", makeButtonsCampus());
        statusMap.put(chatId, Status.SELECT_CAMPUS);
    }

    private void selectCampus() {
        newUser.setCampus(controller.getCampus().findByName(input));
        newUser.setRole(controller.getRole().findByName("студент"));
        newUser.setTelegramId(chatId);
        controller.getUser().save(newUser);

        SendMessage send = new SendMessage(chatId.toString(), "Вы успешно зарегистрированы:"
                + '\n' + "логин: " + newUser.getLogin() + '\n'
                + "имя: " + newUser.getName() + '\n' + "кампус: " + newUser.getCampus() + '\n'
                + "роль: " + newUser.getRole());
        send.setReplyMarkup(KeyboardMaker.makeStudentKeyboard());
        botService.sendCustom(send);

        statusMap.put(chatId, Status.BEGIN);
        isFinished = true;
    }

    private enum Status {
        BEGIN,
        ENTER_LOGIN,
        ENTER_NAME,
        SELECT_CAMPUS
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

}