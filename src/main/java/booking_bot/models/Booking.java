package booking_bot.models;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private Status status;
    private BookObject bookObject;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", status=" + status +
                ", bookObject=" + bookObject +
                ", user=" + user +
                '}';
    }
}
