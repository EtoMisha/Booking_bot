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

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            input = update.getCallbackQuery().getData();
        } else {
            chatId = update.getMessage().getChatId();
            input = update.getMessage().getText();
        }

        System.out.println("[start : execute] begin chatId " + chatId + " input " + input + " begin " + begin);

//        statusMap.putIfAbsent(chatId, Status.BEGIN);
        if (begin) {
            statusMap.put(chatId, Status.BEGIN);
        }

        currentStatus = begin ? Status.BEGIN : statusMap.get(chatId);

        System.out.println("[start : execute] currentStatus " + currentStatus + " statusMap " + statusMap);

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
        System.out.println("[start : execute] end");


        return isFinished;
    }

    private void begin() {
        System.out.println("[start : begin] begin");
        try {
            newUser = controller.getUser().findByTelegram(chatId);
            SendMessage send = new SendMessage(chatId.toString(), "Привет, " + newUser.getName());

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
        System.out.println("[start : begin] end");

    }

    private void enterLogin() {
        System.out.println("[start : enterLogin] begin");

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

        System.out.println("[start : enterLogin] end");

    }

    private void enterName() {
        System.out.println("[start : enterName] begin");

        newUser.setName(input);
        botService.sendMessage(chatId, "Ок, а теперь выберите кампус", makeButtonsCampus());
        statusMap.put(chatId, Status.SELECT_CAMPUS);
        System.out.println("[start : enterName] end");

    }

    private void selectCampus() {
        System.out.println("[start : selectCampus] begin");

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
        System.out.println("[start : selectCampus] end");

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