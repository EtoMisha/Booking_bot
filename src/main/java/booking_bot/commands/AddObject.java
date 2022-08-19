package booking_bot.commands;

import booking_bot.models.BookObject;
import booking_bot.models.Campus;
import booking_bot.models.Type;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class AddObject extends CommandParent {

    private String commandName;
    private BookObject newObject;

    public AddObject(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        super(sendMessageService, repository, commandContainer);
        this.commandName = "Добавить объект";
        commandContainer.add(commandName, this);
        newObject = new BookObject();
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

        System.out.println("- addObject: " + input + " status: " + status);



        if (status.equals("begin")) {

            isFinished = false;
            List<String> campuses = getNames(repository.findAll(Campus.class));
            sendMessageService.sendWithKeyboard(chatId, "Выберите кампус", campuses);

            statusMap.put(chatId, "Выбор кампуса");
        } else if (status.equals("Выбор кампуса")) {
            newObject.setCampus((Campus)repository.findByName(input, Campus.class));

            List<String> buttons = getNames(repository.findAll(Type.class));
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);

            statusMap.put(chatId, "Выбор категории");
        } else if (status.equals("Выбор категории")) {
            newObject.setType((Type)repository.findByName(input, Type.class));

            sendMessageService.send(chatId, "Введите наименование объекта");


            statusMap.put(chatId, "Ввод объекта");
        } else if (status.equals("Ввод объекта")) {
            newObject.setName(input);

            sendMessageService.send(chatId, "Вы добавили " + input);

            //TODO сохранить наименование объекта в базу
            repository.save(newObject, newObject.getClass());

            statusMap.put(chatId, "begin");
            isFinished = true;
        }

        return isFinished;
    }


    @Override
    public String getCommandName() {
        return commandName;
    }



}
