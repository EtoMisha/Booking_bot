package booking_bot.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandContainer {
    private final Map<String, Command> commandMap;

    public CommandContainer () {
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
