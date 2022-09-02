package booking_bot.commands;

import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

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
public class Admin extends CommandParent {

    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    private User newUser;
    private static final String PASSWORD = "Password"; // Временный пароль

    public Admin(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.ADMIN.getText();
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
            botService.sendMessage(chatId, "Введи пароль:", null);
            statusMap.put(chatId, Status.ENTER_PASSWORD);
        } else if (currentStatus.equals(Status.ENTER_PASSWORD)) {
            if (input.equals(PASSWORD)) {
                try {
                    newUser = controller.getUser().findByTelegram(chatId);
                    SendMessage send = new SendMessage(chatId.toString(), "Привет, " + newUser.getName() + ". Что будем делать?");
                    send.setReplyMarkup(adminKeyboard());

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

        } else if (currentStatus.equals(Status.ENTER_LOGIN)) {
            newUser.setLogin(input.toLowerCase());
            List<User> usersList = controller.getUser().findAll();

            boolean isExist = false;
            for(User obj : usersList) {
                if (obj.getLogin().equals(input.toLowerCase())) {
                    isExist = true;
                    newUser = obj;
                }
            }
            if (isExist) {
                botService.sendMessage(chatId, "Пользователь " + newUser.getLogin() + " авторизован. Выбери, что хочешь сделать", null);
            } else {
                botService.sendMessage(chatId, "Введи имя пользователя", null);
                statusMap.put(chatId, Status.ENTER_NAME);
            }


        } else if (currentStatus.equals(Status.ENTER_NAME)) {
            newUser.setName(input);
            botService.sendMessage(chatId, "Ок, а теперь выберите кампус", makeButtonsCampus());
            statusMap.put(chatId, Status.ENTER_CAMPUS);


        } else if (currentStatus.equals(Status.ENTER_CAMPUS)) {
            newUser.setCampus(controller.getCampus().findByName(input));
            newUser.setRole(controller.getRole().findByName("Администратор"));

            newUser.setTelegramId(chatId);
            controller.getUser().save(newUser);

            SendMessage send = new SendMessage(chatId.toString(), "Ты успешно зарегистрирован:"
                    + '\n' + "логин: " + newUser.getLogin() + '\n'
                    + "имя: " + newUser.getName() + '\n' + "кампус: " + newUser.getCampus() + '\n'
                    + "роль: " + newUser.getRole());
            send.setReplyMarkup(adminKeyboard());
            botService.sendCustom(send);

            statusMap.put(chatId, Status.BEGIN);
            isFinished = true;
        }

        return isFinished;
    }

    @Override
    public String getCommandName() {
        return "/admin";
    }

    private ReplyKeyboardMarkup adminKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Забронировать");
        keyboardRow1.add("Мои бронирования");

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Добавить объект");
//        keyboardRow2.add("Редактировать каталог");
        keyboardRow2.add("Редактировать объект");
        keyboardRow2.add("Управление пользователями");

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);
        keyBoardRows.add(keyboardRow2);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }

    private ReplyKeyboardMarkup studentKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Забронировать");
        keyboardRow1.add("Отмена бронирования");

        ArrayList<KeyboardRow> keyBoardRows = new ArrayList<>();
        keyBoardRows.add(keyboardRow1);

        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(keyBoardRows);
        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }

    private enum Status {
        BEGIN,
        ENTER_PASSWORD,
        ENTER_LOGIN,
        ENTER_NAME,
        ENTER_CAMPUS
    }
}


