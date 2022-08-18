package booking_bot.commands;

import booking_bot.models.User;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class UserRedact extends CommandParent {

    private String commandName;
    private User userTmp;
    private boolean flagRedact;

    public UserRedact(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        super(sendMessageService, repository, commandContainer);
        this.commandName = "Управление пользователем";
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
        List<String> buttonsCampus = new ArrayList<>();
        buttonsCampus.add("Москва");
        buttonsCampus.add("Казань");
        buttonsCampus.add("Новосибирск");
        return (buttonsCampus);
    }
    public List<String> makeButtonsRole() {
        List<String> buttonsRole = new ArrayList<>();
        buttonsRole.add("Студент");
        buttonsRole.add("Сотрудник");
        return (buttonsRole);
    }
    public List<String> makeButtons() {
        List<String> buttons = new ArrayList<>();
        buttons.add("Роль");
        buttons.add("Кампус");
        return (buttons);
    }
    @Override
    public boolean execute(Update update, boolean begin) {

        boolean user = false; //  получить данные из БД
        //boolean user = true; //  получить данные из БД
        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }
        System.out.println("- user redact: input " + input);

        if (status.equals("begin")) {
            isFinished = false;
            sendMessageService.send(chatId, "Введите логин");


            statusMap.put(chatId, "Ввод логина");
        } else if (status.equals("Ввод логина")) {

            userTmp.setLogin(input);
            // TODO Проверка в БД добавлен ли уже пользователь с таким ником
            //Если пользователь существует
            if (user) { //  получить данные из БД
                sendMessageService.sendWithKeyboard(chatId, "Пользователь с ником " + userTmp.getLogin() + " найден. Вы можете удалить или отредактировать пользователя.", makeButtonsRed());
                statusMap.put(chatId, "Редактирование");
            }
            //Если пользователь не существует
            if (!user) { // получить данные из БД
                sendMessageService.sendWithKeyboard(chatId, "Пользователь  " + userTmp.getLogin() + " с таким ником не существует. Вы можете добавить пользователя.", makeButtonsAdd());
                statusMap.put(chatId, "Добавление");
            }

        } else if (status.equals("Редактирование")) {

            if (input.equals("Редактировать")) {
                //TODO получить пользователя из БД????

                sendMessageService.sendWithKeyboard(chatId, "Что будем редактировать?", makeButtons());
                statusMap.put(chatId, "Выбор поля");
            }

            if (input.equals("Удалить")) {
                //TODO удалить пользователя из БД
                sendMessageService.send(chatId, "Пользователь " + userTmp.getLogin() + " удален");


                statusMap.put(chatId, "begin");
                isFinished = true;
            }

        } else if (status.equals("Выбор поля")) {
            if (input.equals("Роль")) {
                flagRedact = true;
                sendMessageService.sendWithKeyboard(chatId, "Выберите роль", makeButtonsRole());
                statusMap.put(chatId, "Роль");
            }
            if (input.equals("Кампус")) {
                flagRedact = true;
                sendMessageService.sendWithKeyboard(chatId, "Выберите кампус", makeButtonsCampus());
                statusMap.put(chatId, "Кампус");
            }

        } else if (status.equals("Добавление")) {

            if (input.equals("Добавить")) {
                sendMessageService.send(chatId, "Введите имя пользователя");

                statusMap.put(chatId, "Ввод имени");
            }

        } else if (status.equals("Ввод имени")) {
            userTmp.setName(input);
            sendMessageService.sendWithKeyboard(chatId, "Ок, а теперь выберите кампус", makeButtonsCampus());
            statusMap.put(chatId, "Кампус");

        } else if (status.equals("Кампус")) {
            userTmp.setCampus(input);
            if (flagRedact){
                sendMessageService.send(chatId, "Пользователь отредактирован" +'\n' + "логин: " + userTmp.getLogin() + '\n' + "имя: " + userTmp.getName() + '\n' + "кампус: " + userTmp.getCampus() + '\n' + "роль: " + userTmp.getRole());

                statusMap.put(chatId, "begin");
                isFinished = true;
            } else {
                sendMessageService.sendWithKeyboard(chatId, "Осталось выбрать роль", makeButtonsRole());
                statusMap.put(chatId, "Роль");
            }

        } else if (status.equals("Роль")) {

            userTmp.setRole(input);
            if (flagRedact) {
                sendMessageService.send(chatId, "Пользователь отредактирован" +'\n' + "логин: " + userTmp.getLogin() + '\n' + "имя: " + userTmp.getName() + '\n' + "кампус: " + userTmp.getCampus() + '\n' + "роль: " + userTmp.getRole());
            } else {
                sendMessageService.send(chatId, "Новый пользователь добавлен:" + '\n' + "логин: " + userTmp.getLogin() + '\n' + "имя: " + userTmp.getName() + '\n' + "кампус: " + userTmp.getCampus() + '\n' + "роль: " + userTmp.getRole());
                //TODO добавить пользователя в БД
            }
            statusMap.put(chatId, "begin");
            isFinished = true;
        }

            return isFinished;
        }

        @Override
        public String getCommandName () {
            return commandName;
        }
}
