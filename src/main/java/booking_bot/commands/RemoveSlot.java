package booking_bot.commands;

import booking_bot.models.Slot;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RemoveSlot extends CommandParent {

    public RemoveSlot(SendMessageService sendMessageService, Repository<Slot> repository) {
        super(sendMessageService, repository);
    }

    @Override
    public Status execute(Update update) {
        System.out.println("-- Remove slot: start, status: " + status);

        Long chatId = update.getMessage().getChatId();

        if (status == Status.START) {
            SendMessage send = new SendMessage();
            send.setChatId(chatId.toString());
            send.setText("Выберите какой слот удалить");
            send.setReplyMarkup(initButtons());

            sendMessageService.sendCustom(send);
            status = Status.REMOVE_SLOT;

        } else if (status == Status.REMOVE_SLOT) {
            String message = update.getCallbackQuery().getData();
            LocalDateTime localDateTime = LocalDateTime.parse(message);
            try {
                repository.delete(new Slot(localDateTime));
                sendMessageService.send(chatId, "Готово");
                status = Status.START;
            } catch (DataAccessException e) {
                System.err.println(e.getMessage());
                sendMessageService.send(chatId, "Упс, не получилось удалить слот, попробуйте позже");
            }
        }

        return status;
    }

    InlineKeyboardMarkup initButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();
        List<Slot> slotList = repository.findAll();

        slotList.sort(Slot::compareTo);

        for (Slot slot : slotList) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
            String slotString = slot.getDate().toLocalDate() + " " + slot.getDate().toLocalTime();

            InlineKeyboardButton button = new InlineKeyboardButton(slotString);
            button.setCallbackData(slot.getDate().toString());
            keyboardButtonRow.add(button);
            totalList.add(keyboardButtonRow);
        }
        inlineKeyboardMarkup.setKeyboard(totalList);

        return inlineKeyboardMarkup;
    }

    @Override
    public void setStatus(Status status) {

    }
}
