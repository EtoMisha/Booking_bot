package booking_bot.commands.impls;

import booking_bot.commands.CommandContainer;
import booking_bot.commands.CommandNames;
import booking_bot.commands.CommandParent;
import booking_bot.models.Booking;
import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookings extends CommandParent {
    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    public MyBookings(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.ADMIN.getText();
        commandContainer.add(commandName, this);
        statusMap = new HashMap<>();
    }

    public boolean execute(Update update, boolean begin) {
        prepare(update);

        statusMap.putIfAbsent(chatId, Status.BEGIN);
        if (begin) {
            statusMap.put(chatId, currentStatus);
        }

        currentStatus = statusMap.get(chatId);

        if (currentStatus.equals(Status.BEGIN)) {
            isFinished = false;
            begin();
        } else if (currentStatus.equals(Status.DELETE)) {
            delete();
        }

        return isFinished;
    }

    private void begin() {
        User user = controller.getUser().findByTelegram(chatId);
        List<Booking> bookingList = controller.getBooking().findByUser(user);

        if (bookingList.isEmpty()) {
            botService.sendMessage(chatId, "У вас пока нет бронирований", null);
            statusMap.put(chatId, Status.BEGIN);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            for (Booking booking : bookingList) {

                SendMessage sendMessage = new SendMessage();
                String text = "*" + booking.getBookObject().getName() + "*\n"
                        + booking.getTimeStart().toLocalDate().format(formatter) + "\n"
                        + booking.getTimeStart().toLocalTime() + " - " + booking.getTimeEnd().toLocalTime();

                sendMessage.setText(text);
                sendMessage.setParseMode("markdown");
                sendMessage.setChatId(chatId.toString());
                sendMessage.setReplyMarkup(makeCancelButton(String.valueOf(booking.getId())));

                botService.sendCustom(sendMessage);
            }
        }

        statusMap.put(chatId, Status.DELETE);
    }

    private void delete() {
        Booking bookingToDelete = new Booking();
        bookingToDelete.setId(Integer.parseInt(input));

        controller.getBooking().delete(bookingToDelete);
        botService.sendMessage(chatId, "Готово", null);

        statusMap.put(chatId, Status.BEGIN);
    }

    private InlineKeyboardMarkup makeCancelButton(String callBackText) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("Удалить");
        button.setCallbackData(callBackText);
        keyboardButtonRow.add(button);
        totalList.add(keyboardButtonRow);
        inlineKeyboardMarkup.setKeyboard(totalList);

        return inlineKeyboardMarkup;
    }

    private enum Status {
        BEGIN,
        DELETE
    }

    public String getCommandName() {
        return commandName;
    }

}
