package booking_bot.commands;

import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class StartCommand extends CommandParent {

    private User userTmp;
    public StartCommand(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        super(sendMessageService, controller, commandContainer);
        commandContainer.add("/start", this);
        userTmp = new User();
    }

    public List<String> makeButtonsCampus() {
        return (getNames(controller.getCampus().findAll()));
    }

    /*
     *   Поля унаследованные от родительского класса:
     *
     *   Long chatId - ID чата с пользователем
     *   String input - сообщение от пользователя
     *   String status - текущий статус, в начале он всегда "begin", а дальше надо перезаписывать на каждом шаге
     *   boolean isFinished - обозначает что весь сценарий прошли, в начале false, в самом конце меняем на true
     */
    @Override
    public boolean execute(Update update, boolean begin) {

        boolean isUser = false;
        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }

        if (status.equals("begin")) {

            try {
                userTmp = controller.getUser().findByTelegram(chatId);
                SendMessage send = new SendMessage(chatId.toString(), "Привет, "+ userTmp.getName());
                send.setReplyMarkup(studentKeyboard());
                sendMessageService.sendCustom(send);

                statusMap.put(chatId, "begin");
                isFinished = true;
            } catch (DataAccessException ex) {
                sendMessageService.send(chatId, "Привет, это бот Школы 21 для бронирования помещений инвентаря и вообще всего.\n");
                sendMessageService.send(chatId, "Авторизуйтесь.");
                statusMap.put(chatId, "Ввод логина");
            }

            // выполнение шага 1
//            sendMessageService.send(chatId, "Привет, это бот. Авторизуйтесь.");
//            statusMap.put(chatId, "Ввод логина");


        } else if (status.equals("Ввод логина")) {
           // System.out.println("AAAAAA");
            userTmp.setLogin(input);
            List<User> usersList = controller.getUser().findAll();

            for(User obj : usersList) {
                if (obj.getLogin().equals(input)) {
                    isUser = true;
                    userTmp = obj;
                }
            }
            if (isUser) {
                sendMessageService.send(chatId, "Пользователь " + userTmp.getLogin() + " авторизован. Можете бронировать.");
            }
            if (!isUser) {
                sendMessageService.send(chatId, "Пользователь c ником " + userTmp.getLogin() + " не существует. Вы можете зарегистрироваться.");
                sendMessageService.send(chatId, "Введите имя пользователя");

                statusMap.put(chatId, "Ввод имени");
            }


        } else if (status.equals("Ввод имени")) {
            userTmp.setName(input);
            sendMessageService.sendWithKeyboard(chatId, "Ок, а теперь выберите кампус", makeButtonsCampus());
            statusMap.put(chatId, "Кампус");


        } else if (status.equals("Кампус")) {
            userTmp.setCampus(controller.getCampus().findByName(input));
            userTmp.setRole(controller.getRole().findByName("студент"));

            userTmp.setTelegramId(chatId);
            controller.getUser().save(userTmp);
            sendMessageService.send(chatId, "Вы успешно зарегистрированы:"
                    + '\n' + "логин: " + userTmp.getLogin() + '\n'
                    + "имя: " + userTmp.getName() + '\n' + "кампус: " + userTmp.getCampus() + '\n'
                    + "роль: " + userTmp.getRole());


            statusMap.put(chatId, "begin");
            isFinished = true;
        }

        return isFinished;
    }

    @Override
    public String getCommandName() {
        return "/start";
    }

    private ReplyKeyboardMarkup adminKeyboard() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Забронировать");
        keyboardRow1.add("Отмена бронирования");

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Добавить объект");
//        keyboardRow2.add("Редактировать каталог");
        keyboardRow2.add("Редактировать объект");
        keyboardRow2.add("Управление пользователем");

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

}