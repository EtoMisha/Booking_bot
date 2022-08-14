package booking_bot.commands;

import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Start implements Command {

    private final SendMessageService sendMessageService;
    private Status status;

    public Start(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public Status execute(Update update) {
        Long chatId = update.getMessage().getChatId();

        sendMessageService.send(chatId, "Привет, это админка бота");
        return Status.START;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}
