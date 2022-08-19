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

    public RedactObject(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        super(sendMessageService, controller, commandContainer);
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
            List<String> campuses = getNames(controller.getCampus().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите кампус", campuses);

            statusMap.put(chatId, "Выбор кампуса");
        } else if (status.equals("Выбор кампуса")) {
            tmpObject.setCampus(controller.getCampus().findByName(input));

            List<String> buttons = getNames(controller.getType().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);

            statusMap.put(chatId, "Выбор категории");
        } else if (status.equals("Выбор категории")) {

            List<String> objects = getNames(controller.getBookingObject().findByType(input));
            sendMessageService.sendWithKeyboard(chatId, "Выберите объект", objects);

            statusMap.put(chatId, "Выбор объекта");
        } else if (status.equals("Выбор объекта")) {
            tmpObject.setName(input);

            List<String> actions = new ArrayList<>();
            actions.add("Изменить наименование объекта");
            actions.add("Изменить описание объекта");
            actions.add("Удалить объект");

            sendMessageService.sendWithKeyboard(chatId, "Выберите действие", actions);

            statusMap.put(chatId, "Выбор действия");
        } else if (status.equals("Выбор действия")) {
            if (input.equals("Изменить наименование объекта")) {
                sendMessageService.send(chatId, "Введите новое наименование объекта");
                statusMap.put(chatId, "Изменение наименование объекта");
            }
            else if (input.equals("Изменить описание объекта")) {
                sendMessageService.send(chatId, "Введите новое описание объекта");
                statusMap.put(chatId, "Изменение описание объекта");
            }
            else if (input.equals("Удалить объект")) {
                sendMessageService.send(chatId, "Вы удалили объект " + tmpObject.getName());
                statusMap.put(chatId, "Удаление объекта");
            }
        } else if (status.equals("Изменение наименование объекта")) {

            tmpObject.setName(input);
            controller.getBookingObject().update(tmpObject);
            //TODO сохранить новое наименование объекта в базу
            sendMessageService.send(chatId, "Вы изменили на " + input);

            statusMap.put(chatId, "begin");
            isFinished = true;
        } else if (status.equals("Изменение описание объекта")) {
            tmpObject.setDescription(input);
            controller.getBookingObject().update(tmpObject);
            //TODO сохранить новое описание объекта в базу
            sendMessageService.send(chatId, "Вы изменили описание объекта " + tmpObject.getName());

            statusMap.put(chatId, "begin");
            isFinished = true;
        } else if (status.equals("Удаление объекта")) {
            controller.getBookingObject().delete(tmpObject);
            //TODO удалить объект из базы
//            sendMessageService.send(chatId, "Объект " + tmpObject.getName() + " удален");

//            sendMessageService.send(chatId, "Вы удалили объект " + tmpObject.getName());

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
