package booking_bot;

import booking_bot.commands.Command;
import booking_bot.commands.CommandContainer;
import booking_bot.commands.CommandNames;
import booking_bot.commands.Status;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {

    private final String USERNAME;
    private final String TOKEN;

    private CommandContainer commandContainer;
    private final Map<Long, Status> statusMap;

    public Bot(String username, String token) {
        this.USERNAME = username;
        this.TOKEN = token;
        statusMap = new HashMap<>();
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
        Status status = statusMap.get(chatId);
        Status newStatus = status;
        Command command;

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            System.out.println("bot: message: " + message);
        }
        if (update.hasCallbackQuery()) {
            String callback = update.getCallbackQuery().getData();
            System.out.println("bot: Callback data: " + callback);
        }
        

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();




            if (!statusMap.containsKey(chatId)) {
                statusMap.put(chatId, Status.START);

            }

            System.out.println("bot: id " + chatId + ", status " + statusMap.get(chatId));

            if (commandContainer.hasCommand(message)) {
                status = Status.START;
                System.out.println("bot: get command: " + message);
                command = commandContainer.getCommand(message);
//                newStatus = execute(update);
            } else if (status != Status.START) {
//                System.out.println("bot: get command by status: " + status);
                command = commandContainer.getCommandByStatus(status);
            } else {
                command = commandContainer.getCommand(CommandNames.UNKNOWN.getText());
            }

            command.setStatus(status);
            newStatus = command.execute(update);


        } else if (update.hasCallbackQuery()) {
            System.out.println("bot: Callback start");
            String message = update.getCallbackQuery().getData();

            System.out.println("bot: Callback data: " + message);
            command = commandContainer.getCommand(CommandNames.REMOVE_SLOT.getText());
            command.setStatus(Status.REMOVE_SLOT);
            newStatus = command.execute(update);
        }

        statusMap.put(chatId, newStatus);
    }

    public void setCommandContainer(CommandContainer commandContainer) {
        this.commandContainer = commandContainer;
    }
}