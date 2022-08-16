package booking_bot.commands;

import booking_bot.models.Slot;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class CommandContainer {
    private final Map<String, Command> commandMap;

    public CommandContainer (SendMessageService sendMessageService, Repository<Slot> slotRepository) {
        commandMap = new HashMap<>();
    }

    public Command getCommand(String commandName) {
        return commandMap.get(commandName);
    }

    public void add(String commandName, Command command) {
        commandMap.put(commandName, command);
    }

    public boolean hasCommand(String name) {
        return commandMap.containsKey(name);
    }

}