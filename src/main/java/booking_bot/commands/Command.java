package booking_bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    Status execute(Update update);
    void setStatus(Status status);
}
