package booking_bot.commands;

import booking_bot.models.Slot;
import booking_bot.repositories.Repository;
import booking_bot.services.SendMessageService;
import org.springframework.dao.DataAccessException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class NewSlot extends CommandParent {

    LocalDateTime localDateTime;
    LocalDate localDate;

    public NewSlot(SendMessageService sendMessageService, Repository<Slot> repository) {
        super(sendMessageService, repository);
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status execute(Update update) {
        System.out.println("-- NewSlot: start");

        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText().trim();

//        Status newStatus = status;
        String buttonTextEnough = "На этот день хватит";
        String buttonTextCancel = "Отменить";

        if (status == Status.START) {
            status = Status.NEW_SLOT_DATE;
            sendMessageService.sendWithButton(chatId, "Введите дату в формате ДД.ММ.ГГГГ, например 17.10.2022", buttonTextCancel);

        } else if (message.equals(buttonTextEnough) || message.equals(buttonTextCancel)) {
            status = Status.START;
            sendMessageService.send(chatId, "Ок");

        } else if (status == Status.NEW_SLOT_DATE) {
            System.out.println("-- NewSlot: start slot date");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            try {
                localDate = LocalDate.parse(message, dateFormatter);

                System.out.println("-- NewSlot: date ok");

                sendMessageService.sendWithButton(chatId,"Ок, теперь можно задать несколько слотов на этот день. Напишите время в формате ЧЧ.ММ", buttonTextEnough);
                status =  Status.NEW_SLOT_TIME;
            } catch (DateTimeParseException e) {
                System.err.println(e.getMessage());
                sendMessageService.send(chatId, "Не получилось, проверьте что формат даты правильный");
            }

        } else if (status == Status.NEW_SLOT_TIME) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
            try {
                LocalTime localTime = LocalTime.parse(message, timeFormatter);
                localDateTime = LocalDateTime.of(localDate, localTime);
                repository.save(new Slot(localDateTime));

                System.out.println("-- NewSlot: time ok");
                sendMessageService.sendWithButton(chatId,"Ок, добавлен слот:\n"
                        + localDateTime.toLocalDate() + " " + localDateTime.toLocalTime(), buttonTextEnough);

            } catch (DateTimeParseException e) {
                System.err.println(e.getMessage());
                sendMessageService.sendWithButton(chatId, "Не получилось, проверьте что формат времени правильный", buttonTextEnough);

            } catch (DataAccessException e) {
                e.printStackTrace();
                sendMessageService.send(chatId, "Упс, не получилось сохранить слот, попробуйте позже");
            }
        }

        return status;
    }
}
