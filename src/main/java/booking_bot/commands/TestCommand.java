package booking_bot.commands;

import booking_bot.models.Campus;
import booking_bot.models.User;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

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

        System.out.println("- test: " + input + " status: " + status);

        if (status.equals("begin")) {
            isFinished = false;
            sendMessageService.send(chatId, "Привет");

            try {
                List<String> buttons = getNames(repository.findAll(User.class));
                System.out.println(Arrays.toString(buttons.toArray()));
                sendMessageService.sendWithKeyboard(chatId, "Ок, а теперь выбери что-нибудь", buttons);
                statusMap.put(chatId, "выбор с кнопки");
            } catch (DataAccessException e) {
                sendMessageService.send(chatId, "Ошибка с БД");
                e.printStackTrace();
                statusMap.put(chatId, "begin");
                isFinished = true;
            }


        } else if (status.equals("выбор с кнопки")) {


            sendMessageService.send(chatId, "Вы нажали " + input);

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
