package booking_bot.commands;

import booking_bot.models.Slot;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;

import java.util.HashMap;
import java.util.Map;

public class CommandContainer {
    private final Map<String, Command> commandMap;

    public CommandContainer (SendMessageService sendMessageService, Repository<Slot> slotRepository) {

        commandMap = new HashMap<>();
        commandMap.put(CommandNames.START.getText(), new Start(sendMessageService));
        commandMap.put(CommandNames.NEW_SLOT.getText(), new NewSlot(sendMessageService, slotRepository));
        commandMap.put(CommandNames.REMOVE_SLOT.getText(), new RemoveSlot(sendMessageService, slotRepository));
//        commandMap.put(CommandNames.SHOW_SLOTS.getText(), new ShowSlots(sendMessageService));
//        commandMap.put(CommandNames.SETUP_SLOTS.getText(), new SetupSlots(sendMessageService));
//        commandMap.put(CommandNames.SETUP_NOTIFICATIONS.getText(), new SetupNotifications(sendMessageService));
        commandMap.put(CommandNames.UNKNOWN.getText(), new Unknown(sendMessageService));
    }

    public Command getCommand(String name) {
//        if (!commandMap.containsKey(name)) {
//            name = CommandNames.UNKNOWN.getText();
//        }

        return commandMap.get(name);
    }

    public Command getCommandByStatus(Status status) {
        if (status == Status.NEW_SLOT_DATE || status == Status.NEW_SLOT_TIME) {
            System.out.println("container: run new slot");

            return commandMap.get(CommandNames.NEW_SLOT.getText());
        } else {
            System.out.println("container: run default");

            return commandMap.get(CommandNames.START.getText());
        }
    }

    public boolean hasCommand(String name) {
        return commandMap.containsKey(name);
    }
}
