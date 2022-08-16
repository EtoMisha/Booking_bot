package booking_bot;

import booking_bot.commands.Command;
import booking_bot.commands.CommandContainer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {

    private final String USERNAME;
    private final String TOKEN;

    private CommandContainer commandContainer;
    private final Map<Long, String> commandMap;

    public Bot(String username, String token) {
        this.USERNAME = username;
        this.TOKEN = token;
        commandMap = new HashMap<>();
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Long chatId = update.getMessage().getChatId();
        String input = update.getMessage().getText();

        System.out.println("----\nbot: text: " + input);

//        if (!commandMap.containsKey(chatId)) {
//            commandMap.put(chatId, "/start");
//        }

        Command command;
        String commandFromMap = commandMap.get(chatId);
        boolean isFinished;
        if (commandContainer.hasCommand(input)) {

            command = commandContainer.getCommand(input);
            commandMap.put(chatId, command.getCommandName());
            isFinished = command.execute(update, true);
            System.out.println("bot: command from container: " + command.getCommandName() + " true");
        } else {
            command = commandContainer.getCommand(commandFromMap);
            isFinished = command.execute(update, false);
            System.out.println("bot: command from map: " + command.getCommandName() + "false");
        }

        if (isFinished) {
            commandMap.remove(chatId);
        }


    }

    public void setCommandContainer(CommandContainer commandContainer) {
        this.commandContainer = commandContainer;
    }

}