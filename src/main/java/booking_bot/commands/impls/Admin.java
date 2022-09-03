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
import java.util.Map;

/*
 *   Поля унаследованные от родительского класса:
 *
 *   Long chatId - ID чата с пользователем
 *   String input - сообщение от пользователя
 *   String status - текущий статус, в начале он всегда "begin", а дальше надо перезаписывать на каждом шаге
 *   boolean isFinished - обозначает что весь сценарий прошли, в начале false, в самом конце меняем на true
 */
public class Admin extends CommandParent {

    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    private User newUser;
    private String adminPassword; // Временный пароль

    public Admin(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.ADMIN.getText();
        commandContainer.add(commandName, this);
        statusMap = new HashMap<>();

        newUser = new User();
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
        } else if (currentStatus.equals(Status.ENTER_PASSWORD)) {
            enterPassword();
        } else if (currentStatus.equals(Status.ENTER_LOGIN)) {
            enterLogin();
        } else if (currentStatus.equals(Status.ENTER_NAME)) {
            enterName();
        } else if (currentStatus.equals(Status.ENTER_CAMPUS)) {
            enterCampus();
        }

        return isFinished;
    }

    private void begin() {
        botService.sendMessage(chatId, "Введи пароль:", null);
        statusMap.put(chatId, Status.ENTER_PASSWORD);
    }

    private void enterPassword() {
        if (input.equals(adminPassword)) {
            try {
                newUser = controller.getUser().findByTelegram(chatId);
                SendMessage send = new SendMessage(chatId.toString(), "Привет, " + newUser.getName() + ". Что будем делать?");
                send.setReplyMarkup(KeyboardMaker.makeAdminKeyboard());
                botService.sendCustom(send);
                statusMap.put(chatId, Status.BEGIN);

            } catch (DataAccessException ex) {
                botService.sendMessage(chatId, "Привет, это бот Школы 21 для бронирования помещений инвентаря и вообще всего.\n", null);
                botService.sendMessage(chatId, "Давай зарегистрируем тебя. Введи логин", null);
                statusMap.put(chatId, Status.ENTER_LOGIN);
            }
        } else {
            botService.sendMessage(chatId, "Упссс... Пароль неверный. Лучше начни с команды /start", null);
            statusMap.put(chatId, Status.BEGIN);
        }
    }

    private void enterLogin() {
        newUser = controller.getUser().findByName(input);

        if (newUser != null) {
            botService.sendMessage(chatId, "Пользователь " + newUser.getLogin() + " авторизован. Выбери, что хочешь сделать", null);
        } else {
            botService.sendMessage(chatId, "Введи имя пользователя", null);
            statusMap.put(chatId, Status.ENTER_NAME);
        }
    }

    private void enterName() {
        newUser.setName(input);
        botService.sendMessage(chatId, "Ок, а теперь выберите кампус", getNames(controller.getCampus().findAll()));
        statusMap.put(chatId, Status.ENTER_CAMPUS);
    }

    private void enterCampus() {
        newUser.setCampus(controller.getCampus().findByName(input));
        newUser.setRole(controller.getRole().findByName("Администратор"));

        newUser.setTelegramId(chatId);
        controller.getUser().save(newUser);

        SendMessage send = new SendMessage(chatId.toString(), "Ты успешно зарегистрирован:"
                + '\n' + "логин: " + newUser.getLogin() + '\n'
                + "имя: " + newUser.getName() + '\n' + "кампус: " + newUser.getCampus() + '\n'
                + "роль: " + newUser.getRole());
        send.setReplyMarkup(KeyboardMaker.makeAdminKeyboard());
        botService.sendCustom(send);

        statusMap.put(chatId, Status.BEGIN);
        isFinished = true;
    }

    private enum Status {
        BEGIN,
        ENTER_PASSWORD,
        ENTER_LOGIN,
        ENTER_NAME,
        ENTER_CAMPUS
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    public void setAdminPassword(String password) {
        this.adminPassword = password;
    }
}


