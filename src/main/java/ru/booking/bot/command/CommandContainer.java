package ru.booking.bot.command;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommandContainer {

    private final ApplicationContext context;
    private final Map<String, Class<? extends Command>> commandClasses = new HashMap<>();

    public CommandContainer(ApplicationContext context) {
        this.context = context;

        for (CommandName commandName : CommandName.values()) {
            commandClasses.put(commandName.getText(), commandName.getClassName());
        }
    }

    public Command getCommand(String commandName) {
        return context.getBean(commandClasses.get(commandName));
    }

    public boolean hasCommand(String name) {
        return commandClasses.containsKey(name);
    }

}
