package booking_bot.commands;

import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

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
            sendMessageService.send(chatId, "Введите имя пользователя");

            statusMap.put(chatId, "Ввод имени");
        } else if (status.equals("Ввод имени")) {

            sendMessageService.send(chatId, "Вы ввели " + input);
            // сохранить имя в базу

            List<String> buttons = new ArrayList<>();
            buttons.add("кнопка 1");
            buttons.add("кнопка 2");
            sendMessageService.sendWithKeyboard(chatId, "Ок, а теперь выбери что-нибудь", buttons);


            statusMap.put(chatId, "выбор с кнопки");
        } else if (status.equals("выбор с кнопки")) {


            sendMessageService.send(chatId, "Вы нажили " + input);

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
