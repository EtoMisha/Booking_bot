package booking_bot.commands;

import booking_bot.models.BookObject;
import booking_bot.models.Campus;
import booking_bot.models.Type;
import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class AddObject extends CommandParent {

    private final String commandName;
    private BookObject newObject;

    public AddObject(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        super(sendMessageService, controller, commandContainer);
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

        boolean isObject = false;

        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }

        System.out.println("- addObject: " + input + " status: " + status);

        if (status.equals("begin")) {

            isFinished = false;
            List<String> campuses = getNames(controller.getCampus().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите кампус", campuses);

            statusMap.put(chatId, "Выбор кампуса");
        } else if (status.equals("Выбор кампуса")) {
            newObject.setCampus(controller.getCampus().findByName(input));

            List<String> buttons = getNames(controller.getType().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);

            statusMap.put(chatId, "Выбор категории");
        } else if (status.equals("Выбор категории")) {
            newObject.setType(controller.getType().findByName(input));

            sendMessageService.send(chatId, "Введите наименование объекта");

            statusMap.put(chatId, "Ввод наименования объекта");
        } else if (status.equals("Ввод наименования объекта")) {
            newObject.setName(makeStr(input));

            List<BookObject> objectsList = controller.getBookingObject().findAll();

            for(BookObject obj : objectsList) {
                if (obj.getName().equals(makeStr(input)) && obj.getCampus().getName().equals(newObject.getCampus().getName())) {
                    isObject = true;
                }
            }
            if (isObject) {
                sendMessageService.send(chatId, "Объект " + newObject.getName() + " уже существует. Вы можете удалить или отредактировать его.");
                statusMap.put(chatId, "begin");
                isFinished = true;
            }
            if (!isObject) {
                sendMessageService.send(chatId, "Вы добавили " + input);
                sendMessageService.send(chatId, "Введите описание объекта");
                statusMap.put(chatId, "Ввод описания объекта");
            }

        } else if (status.equals("Ввод описания объекта")) {
            newObject.setDescription(input);
            sendMessageService.send(chatId, "Вы добавили описание " + newObject.getName());
            controller.getBookingObject().save(newObject);

            statusMap.put(chatId, "begin");
            isFinished = true;
        }

        return isFinished;
    }

    public String makeStr(String inputStr){
        return (inputStr.substring(0,1).toUpperCase()+inputStr.substring(1).toLowerCase());
    }

    @Override
    public String getCommandName() {
        return commandName;
    }
}
