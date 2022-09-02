package booking_bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    boolean execute(Update update, boolean begin);
    String getCommandName();

}
