package ru.booking.bot.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    boolean execute(Update update,  boolean isBeginning);
    CommandName getName();
}
