package booking_bot.commands;

import booking_bot.models.Booking;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandParent implements Command {
    protected final SendMessageService sendMessageService;
    protected final Repository<Booking> repository;
    protected final CommandContainer commandContainer;
    protected Map<Long, String> statusMap;
//    protected String commandName;

    protected boolean isFinished;
    protected Long chatId;
    protected String input;
    protected String status;

    public CommandParent(SendMessageService sendMessageService, Repository<Booking> repository, CommandContainer commandContainer) {
        this.sendMessageService = sendMessageService;
        this.repository = repository;
        this.commandContainer = commandContainer;
        this.isFinished = false;
        this.statusMap = new HashMap<>();
    }

    protected void prepare(Update update) {
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            input = update.getCallbackQuery().getData();
        } else {
            chatId = update.getMessage().getChatId();
            input = update.getMessage().getText();
        }

        if (!statusMap.containsKey(chatId)) {
            statusMap.put(chatId, "begin");
        }

        status = statusMap.get(chatId);
    }

//    public String getCommandName() {
//        return commandName;
//    }
}
