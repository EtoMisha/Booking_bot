package booking_bot.commands;

import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Unknown implements Command {

    private final SendMessageService sendMessageService;

    public Unknown(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public Status execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        sendMessageService.send(chatId, "Не знаю такую команду");
        return Status.START;
    }

    @Override
    public void setStatus(Status status) {

    }
}
