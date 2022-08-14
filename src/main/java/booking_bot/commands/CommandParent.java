package booking_bot.commands;

import booking_bot.models.Slot;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;

public abstract class CommandParent implements Command {
    protected final SendMessageService sendMessageService;
    protected final Repository<Slot> repository;
    protected Status status;

    public CommandParent(SendMessageService sendMessageService, Repository<Slot> repository) {
        this.sendMessageService = sendMessageService;
        this.repository = repository;
        this.status = Status.START;
    }
}
