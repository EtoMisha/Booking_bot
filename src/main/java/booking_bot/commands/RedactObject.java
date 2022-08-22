package booking_bot.commands;

import booking_bot.models.BookObject;
import booking_bot.models.Campus;
import booking_bot.models.Type;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class RedactObject extends CommandParent {

    private String commandName;
    private BookObject tmpObject;

    public RedactObject(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        super(sendMessageService, controller, commandContainer);
        this.commandName = "Редактировать объект";
        commandContainer.add(commandName, this);
//        tmpObject = new BookObject();
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

//        BookObject tmpObject = new BookObject();

        if (status.equals("begin")) {

            List<String> buttons = getNames(controller.getType().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);

            statusMap.put(chatId, "Выбор категории");
        } else if (status.equals("Выбор категории")) {

            List<String> objects = getNames(controller.getBookingObject().findByType(input));
            sendMessageService.sendWithKeyboard(chatId, "Выберите объект", objects);

            statusMap.put(chatId, "Выбор объекта");
        } else if (status.equals("Выбор объекта")) {
//            tmpObject.setName(input);

            tmpObject = controller.getBookingObject().findByName(input);

            List<String> actions = new ArrayList<>();
            actions.add("Изменить название");
            actions.add("Изменить описание");
            actions.add("Удалить");
            sendMessageService.sendWithKeyboard(chatId, "Выберите действие", actions);

            statusMap.put(chatId, "Выбор действия");
        } else if (status.equals("Выбор действия")) {
            if (input.equals("Изменить название")) {
                sendMessageService.send(chatId, "Введите новое название");
                statusMap.put(chatId, "Изменение наименование объекта");
            }
            else if (input.equals("Изменить описание")) {
                sendMessageService.send(chatId, "Введите новое описание");
                statusMap.put(chatId, "Изменение описание объекта");
            }
            else if (input.equals("Удалить")) {
                sendMessageService.send(chatId, "Вы удалили объект " + tmpObject.getName());
                controller.getBookingObject().delete(tmpObject);
                statusMap.put(chatId, "Удаление объекта");
            }
        } else if (status.equals("Изменение наименование объекта")) {

            tmpObject.setName(input);

            System.out.println("EDIT OBJEct: " + tmpObject + " id " + tmpObject.getId());
            controller.getBookingObject().update(tmpObject);
            sendMessageService.send(chatId, "Вы изменили на " + input);

            statusMap.put(chatId, "begin");
        } else if (status.equals("Изменение описание объекта")) {
            tmpObject.setDescription(input);
            controller.getBookingObject().update(tmpObject);
            sendMessageService.send(chatId, "Вы изменили описание объекта " + tmpObject.getName());

            statusMap.put(chatId, "begin");
        } else if (status.equals("Удаление объекта")) {
            sendMessageService.send(chatId, "Объект " + tmpObject.getName() + " удален");
            controller.getBookingObject().delete(tmpObject);
            //TODO удалить объект из базы
//            sendMessageService.send(chatId, "Объект " + tmpObject.getName() + " удален");

//            sendMessageService.send(chatId, "Вы удалили объект " + tmpObject.getName());

            statusMap.put(chatId, "begin");
//            isFinished = true;
        }
        return isFinished;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }


}
