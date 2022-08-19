package booking_bot.commands;

import booking_bot.models.BookObject;
import booking_bot.models.HasName;
import booking_bot.models.Type;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommandParent implements Command {
    protected final SendMessageService sendMessageService;
    protected final Controller controller;
    protected final CommandContainer commandContainer;
    protected Map<Long, String> statusMap;

    protected boolean isFinished;
    protected Long chatId;
    protected String input;
    protected String status;

    public CommandParent(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        this.sendMessageService = sendMessageService;
        this.controller = controller;
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

    protected <T> List<String> getNames(List<T> entityList) {
        List<String> names = new ArrayList<>();

        for (T entity : entityList) {
            HasName hasName = (HasName) entity;
            names.add(hasName.getName());
        }

        return names;
    }

//
//    protected List<Object> objectsByType(Type type) {
//
//        List<BookObject> bookObjects = controller.findAll(BookObject.class);
//        List<Object> objectsOfType = new ArrayList<>();
//        for (BookObject obj : bookObjects) {
//            if (obj.getType().equals(type)) {
//                objectsOfType.add(obj);
//            }
//        }
//        return objectsOfType;
//    }

//    public String getCommandName() {
//        return commandName;
//    }
}
