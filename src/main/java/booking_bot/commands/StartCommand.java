package booking_bot.commands;

import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand extends CommandParent {

    public StartCommand(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        super(sendMessageService, repository, commandContainer);
        commandContainer.add("/start", this);
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


            // выполнение шага 1
            sendMessageService.send(chatId, "Привет, это бот");

//            statusMap.put(chatId, "begin");
            isFinished = true;
        }

        return isFinished;
    }

    @Override
    public String getCommandName() {
        return "/start";
    }

}