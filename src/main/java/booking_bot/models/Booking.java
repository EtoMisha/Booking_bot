package booking_bot.models;

import java.time.LocalDateTime;

public class Booking {
    private final int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookObject bookObject;
    private User user;

    public Booking(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public BookObject getBookObject() {
        return bookObject;
    }

    public void setBookObject(BookObject bookObject) {
        this.bookObject = bookObject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
