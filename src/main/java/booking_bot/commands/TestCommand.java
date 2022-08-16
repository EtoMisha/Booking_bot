package booking_bot.commands;

import booking_bot.models.Slot;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TestCommand extends CommandParent {

    private String commandName;

    public TestCommand(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        super(sendMessageService, repository, commandContainer);
        this.commandName = "тест";
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



        if (status.equals("begin")) {
            isFinished = false;
            sendMessageService.send(chatId, "Введите имя пользователя");



            statusMap.put(chatId, "Ввод имени");
        } else if (status.equals("Ввод имени")) {

            sendMessageService.send(chatId, "Вы ввели " + input);
            // сохранить имя в базу
            sendMessageService.send(chatId, "Ок, а теперь введите фамилию ( и тутт кнопочки .... )");


            statusMap.put(chatId, "Ввод фамилии");
        } else if (status.equals("Ввод фамилии")) {


            // выполенние шага 3
            sendMessageService.send(chatId, "Всё ок");


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
