package booking_bot.commands;

import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class EditUser extends CommandParent {

    private String commandName;
    private User userTmp;
    private boolean flagRedact;

    public EditUser(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.EDIT_USER.getText();
        commandContainer.add(commandName, this);
        userTmp = new User(); // ID ???
        flagRedact = false;
    }

    /*
    *   Поля унаследованные от родительского класса:
    *
    *   Long chatId - ID чата с пользователем
    *   String input - сообщение от пользователя
    *   String status - текущий статус, в начале он всегда "begin", а дальше надо перезаписывать на каждом шаге
    *   boolean isFinished - обозначает что весь сценарий прошли, в начале false, в самом конце меняем на true
     */
    
    public List<String> makeButtonsRed() {

        List<String> buttonsRed = new ArrayList<>();
        buttonsRed.add("Редактировать");
        buttonsRed.add("Удалить");
        return (buttonsRed);
    }
    public List<String> makeButtonsAdd() {

        List<String> buttonsAdd = new ArrayList<>();
        buttonsAdd.add("Добавить");
        return (buttonsAdd);
    }
    public List<String> makeButtonsCampus() {
        return (getNames(controller.getCampus().findAll()));
    }
    public List<String> makeButtonsRole() {
        return (getNames(controller.getRoles().findAll()));
    }
    public List<String> makeButtons() {
        List<String> buttons = new ArrayList<>();
        buttons.add("Роль");
        buttons.add("Кампус");
        return (buttons);
    }
    @Override
    public boolean execute(Update update, boolean begin) {

        boolean isUser = false;

        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }
        System.out.println("- user redact: input " + input);

        if (status.equals("begin")) {
            isFinished = false;
            botService.sendMessage(chatId, "Введите логин", null);


            statusMap.put(chatId, "Ввод логина");
        } else if (status.equals("Ввод логина")) {

            userTmp.setLogin(input.toLowerCase());
            List<User> usersList = controller.getUser().findAll();

            for(User obj : usersList) {
                if (obj.getLogin().equals(input.toLowerCase())) {
                    isUser = true;
                    userTmp = obj;
                }
            }
            if (isUser) {
                botService.sendMessage(chatId, "Пользователь с ником " + userTmp.getLogin() +
                        " найден. Вы можете удалить или отредактировать пользователя.", makeButtonsRed());
                statusMap.put(chatId, "Редактирование");
            }
            if (!isUser) {
                botService.sendMessage(chatId, "Пользователь c ником " + userTmp.getLogin() +
                        " не существует. Вы можете добавить пользователя.", makeButtonsAdd());
                statusMap.put(chatId, "Добавление");
            }

        } else if (status.equals("Редактирование")) {

            if (input.equals("Редактировать")) {
                botService.sendMessage(chatId, "Что будем редактировать?", makeButtons());
                statusMap.put(chatId, "Выбор поля");
            }

            if (input.equals("Удалить")) {
                botService.sendMessage(chatId, "Пользователь " + userTmp.getLogin() + " удален", null);
                //удалить пользователя из БД
                controller.getUser().delete(userTmp);

                statusMap.put(chatId, "begin");
                isFinished = true;
            }

        } else if (status.equals("Выбор поля")) {
            if (input.equals("Роль")) {
                flagRedact = true;
                botService.sendMessage(chatId, "Выберите роль", makeButtonsRole());
                statusMap.put(chatId, "Роль");
            }
            if (input.equals("Кампус")) {
                flagRedact = true;
                botService.sendMessage(chatId, "Выберите кампус", makeButtonsCampus());
                statusMap.put(chatId, "Кампус");
            }

        } else if (status.equals("Добавление")) {

            if (input.equals("Добавить")) {
                botService.sendMessage(chatId, "Введите имя пользователя", null);

                statusMap.put(chatId, "Ввод имени");
            }

        } else if (status.equals("Ввод имени")) {
            userTmp.setName(input);
            botService.sendMessage(chatId, "Ок, а теперь выберите кампус", makeButtonsCampus());
            statusMap.put(chatId, "Кампус");

        } else if (status.equals("Кампус")) {
            userTmp.setCampus(controller.getCampus().findByName(input));
            if (flagRedact){
                botService.sendMessage(chatId, "Пользователь отредактирован" +'\n'
                        + "логин: " + userTmp.getLogin() + '\n' + "имя: " + userTmp.getName() + '\n'
                        + "кампус: " + userTmp.getCampus() + '\n' + "роль: " + userTmp.getRole(), null);
                controller.getUser().update(userTmp);
                statusMap.put(chatId, "begin");
                flagRedact = false;
                isFinished = true;
            } else {
                botService.sendMessage(chatId, "Осталось выбрать роль", makeButtonsRole());
                statusMap.put(chatId, "Роль");
            }

        } else if (status.equals("Роль")) {

            userTmp.setRole(controller.getRole().findByName(input));
            if (flagRedact) {
                botService.sendMessage(chatId, "Пользователь отредактирован" +'\n'
                        + "логин: " + userTmp.getLogin() + '\n' + "имя: " + userTmp.getName() + '\n'
                        + "кампус: " + userTmp.getCampus() + '\n' + "роль: " + userTmp.getRole(), null);
                controller.getUser().update(userTmp);
                flagRedact = false;
            } else {
                try {
                    userTmp.setTelegramId(chatId);
                    controller.getUser().save(userTmp);
                    botService.sendMessage(chatId, "Новый пользователь добавлен:"
                            + '\n' + "логин: " + userTmp.getLogin() + '\n'
                            + "имя: " + userTmp.getName() + '\n' + "кампус: " + userTmp.getCampus() + '\n'
                            + "роль: " + userTmp.getRole(), null);
                } catch (DataAccessException e) {
                    botService.sendMessage(chatId, "Не получилось сохранить", null);
                }


            }
            statusMap.put(chatId, "begin");
            isFinished = true;
        }

            return isFinished;
        }

        public String makeStr(String inputStr){
            return (inputStr.substring(0,1).toUpperCase()+inputStr.substring(1).toLowerCase());
        }

        @Override
        public String getCommandName () {
            return commandName;
        }
}
