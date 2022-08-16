package booking_bot.commands;

import booking_bot.models.Slot;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandParent implements Command {
    protected final SendMessageService sendMessageService;
    protected final Repository<Slot> repository;
    protected final CommandContainer commandContainer;
    protected Map<Long, String> statusMap;
//    protected String commandName;

    protected boolean isFinished;
    protected Long chatId;
    protected String input;
    protected String status;

    public CommandParent(SendMessageService sendMessageService, Repository<Slot> repository, CommandContainer commandContainer) {
        this.sendMessageService = sendMessageService;
        this.repository = repository;
        this.commandContainer = commandContainer;
        this.isFinished = false;
        this.statusMap = new HashMap<>();
    }

    protected void prepare(Update update) {
        chatId = update.getMessage().getChatId();

        if (update.hasMessage() && update.getMessage().hasText()) {
            input = update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            input = update.getCallbackQuery().getData();
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
