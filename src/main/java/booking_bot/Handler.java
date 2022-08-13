package booking_bot;

import booking_bot.Repositories.Repository;
import booking_bot.models.Slot;
import org.springframework.dao.DataAccessException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Handler {

    private final Repository<Slot> repository;

    private Date date;
    private LocalDateTime localDateTime;
    private Calendar calendar;

    public Handler(Repository<Slot> repository) {
        this.repository = repository;
        calendar = Calendar.getInstance();
    }

    public String newSlotDate(String message) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy");
        try {
//            date = dateFormat.parse(message);
            localDateTime = LocalDateTime.parse(message, formatter);
            System.out.println("-- date " + localDateTime);
            return "Ок, теперь введите время";
        } catch (DateTimeParseException e) {
            return "Не получилось, проверьте что формат даты правильный";
        }
    }

    public String newSlotTime(String message) {
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH.mm");
        try {
//            Date time = timeFormat.parse(message);
            LocalDateTime localTime = LocalDateTime.parse(message, formatter);
//            localDateTime


//            date.setTime(time.getTime());
            System.out.println("-- date and time " + date);
            repository.save(new Slot(date));
            return "Ок, добавлен слот:\n" + date;
//        } catch (ParseException e) {
//            return "Не получилось, проверьте что формат времени правильный";
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String removeSlot(String message) {
        return null;

    }

    public String setupSlot(String message) {
        return null;
    }

    public String setupNotifications(String message) {
        return null;
    }

    private String removeSlot() {
        return null;
    }

    private String showSlots() {
        List<Slot> slotList = repository.findAll();
        return slotList.toString();
    }

    private String setupSlots() {
        return null;
    }

    private String setupNotifications() {
        return null;
    }

}
