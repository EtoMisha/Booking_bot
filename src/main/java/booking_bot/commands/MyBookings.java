package booking_bot.commands;

import booking_bot.models.BookObject;
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
import java.util.List;

public class MyBookings extends CommandParent {
    private String commandName = "Мои бронирования";

    private BookObject bookObject;
    private Booking booking;
    private User user;

    public MyBookings(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        commandContainer.add(commandName, this);
        bookObject = new BookObject();
        booking = new Booking();
    }

    public boolean execute(Update update, boolean begin) {
        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }

        user = controller.getUser().findByTelegram(chatId);
        System.out.println("-- my bookings: begin, status " + status);

        if (status.equals("begin")) {

            List<Booking> bookingList = controller.getBooking().findByUser(user);
            System.out.println("BEGIN ");
            System.out.println("BEGIN LIST " + bookingList);
            if (bookingList.isEmpty()) {
                botService.sendMessage(chatId, "У вас пока нет бронирований", null);
                statusMap.put(chatId, "begin");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
                for (Booking booking : bookingList) {
                    System.out.println(booking);

                    SendMessage sendMessage = new SendMessage();
                    String text = "*" + booking.getBookObject().getName() + "*\n"
                            + booking.getTimeStart().toLocalDate().format(formatter) + "\n"
                            + booking.getTimeStart().toLocalTime() + " - " + booking.getTimeEnd().toLocalTime();
                    System.out.println("TEXT " + text);
                    sendMessage.setText(text);
                    sendMessage.setParseMode("markdown");
                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setReplyMarkup(makeCancelButton(String.valueOf(booking.getId())));

                    botService.sendCustom(sendMessage);
                }
            }

            System.out.println("BEGIN END");

            statusMap.put(chatId, "Удалить бронь");
        } else if (status.equals("Удалить бронь")) {
            System.out.println("DELETE ");
            Booking bookingToDelete = new Booking();
            bookingToDelete.setId(Integer.parseInt(input));

            System.out.println("BOOKING TO DELETE " + bookingToDelete);
            controller.getBooking().delete(bookingToDelete);
            botService.sendMessage(chatId, "Готово", null);


            statusMap.put(chatId, "begin");
//            isFinished = true;
        }

        return isFinished;
    }

    public String getCommandName() {
        return commandName;
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


}
