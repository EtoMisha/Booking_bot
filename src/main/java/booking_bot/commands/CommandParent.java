package booking_bot.commands;

import booking_bot.models.HasName;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class
CommandParent implements Command {
    protected final BotService botService;
    protected final Controller controller;
    protected final CommandContainer commandContainer;

    protected boolean isFinished;
    protected Long chatId;
    protected String input;

    public CommandParent(BotService botService, Controller controller, CommandContainer commandContainer) {
        this.botService = botService;
        this.controller = controller;
        this.commandContainer = commandContainer;
        this.isFinished = false;
    }

    protected void prepare(Update update) {
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            input = update.getCallbackQuery().getData();
        } else {
            chatId = update.getMessage().getChatId();
            input = update.getMessage().getText();
        }
    }

    protected <T> List<String> getNames(List<T> entityList) {
        List<String> names = new ArrayList<>();

        for (T entity : entityList) {
            HasName hasName = (HasName) entity;
            names.add(hasName.getName());
        }

        return names;
    }

}
