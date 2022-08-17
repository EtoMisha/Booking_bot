package booking_bot.commands;

import booking_bot.models.BookObject;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class RedactObject extends CommandParent {

    private String commandName;

    public RedactObject(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        super(sendMessageService, repository, commandContainer);
        this.commandName = "Редактировать объект";
        commandContainer.add(commandName, this);
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

        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }

        System.out.println("- redactObject: " + input + " status: " + status);

        BookObject tmpObject = new BookObject();

        if (status.equals("begin")) {

            isFinished = false;
            List<String> campuses = new ArrayList<>();
            campuses.add("Москва");
            campuses.add("Казань");
            campuses.add("Новосибирск");
            sendMessageService.sendWithKeyboard(chatId, "Выберите кампус", campuses);

            statusMap.put(chatId, "Выбор кампуса");
        } else if (status.equals("Выбор кампуса")) {
            tmpObject.setCampus(input);

            List<String> buttons = new ArrayList<>();
            buttons.add("Помещения");
            buttons.add("Настольные игры");
            buttons.add("Спортивный инвентарь");

            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);

            statusMap.put(chatId, "Выбор категории");
        } else if (status.equals("Выбор категории")) {
            tmpObject.setCategory(input);

            List<String> objects = new ArrayList<>();
            objects.add("Переговорка");
            objects.add("Атриум");
            objects.add("Игровая");

            sendMessageService.sendWithKeyboard(chatId, "Выберите объект", objects);

            statusMap.put(chatId, "Выбор объекта");
        } else if (status.equals("Выбор объекта")) {
            tmpObject.setName(input);

            sendMessageService.send(chatId, "Введите новое наименование " + input);

            statusMap.put(chatId, "Изменение объекта");
        } else if (status.equals("Изменение объекта")) {
            tmpObject.setName(input);

            sendMessageService.send(chatId, "Вы изменили на " + input);

            statusMap.put(chatId, "begin");
            isFinished = true;
        }

        return isFinished;
    }
    //TODO сохранить наименование объекта в базу


    @Override
    public String getCommandName() {
        return commandName;
    }

}
