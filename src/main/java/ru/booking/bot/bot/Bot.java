package ru.booking.bot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.booking.bot.command.Command;
import ru.booking.bot.command.CommandContainer;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final CommandContainer commandContainer;
    private final Map<Long, Command> currentUserCommands = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Long userId = UpdateUtil.getUserId(update);
        String input = UpdateUtil.getInput(update);

        boolean isFinished = false;
        if (commandContainer.hasCommand(input)) {   // user starts new command
            Command command = commandContainer.getCommand(input);
            currentUserCommands.put(userId, command);
            isFinished = command.execute(update, true);
        } else if (currentUserCommands.containsKey(userId)) {                                    // user continues some command
            isFinished = currentUserCommands.get(userId).execute(update, false);
        }

        if (isFinished) {
            currentUserCommands.remove(userId);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}