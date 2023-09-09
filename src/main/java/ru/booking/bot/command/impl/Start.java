package ru.booking.bot.command.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.booking.bot.bot.BotService;
import ru.booking.bot.bot.UpdateUtil;
import ru.booking.bot.command.Command;
import ru.booking.bot.command.CommandName;
import ru.booking.bot.command.KeyboardMaker;
import ru.booking.bot.models.User;
import ru.booking.bot.service.UserService;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Scope("prototype")
public class Start implements Command {

    private static final String GREETING_TEXT = "Привет, выбери одну из кнопок ниже";

    private final BotService botService;
    private final UserService userService;

    @Override
    public boolean execute(Update update,  boolean isBeginning) {
        Long userId = UpdateUtil.getUserId(update);
        Optional<User> userOpt = userService.findById(userId);
        User user = userOpt.orElseGet(() -> userService.create(update));
        botService.sendMarkup(userId, GREETING_TEXT, KeyboardMaker.getKeyboard(user));
        return true;
    }

    @Override
    public CommandName getName() {
        return CommandName.START;
    }
}