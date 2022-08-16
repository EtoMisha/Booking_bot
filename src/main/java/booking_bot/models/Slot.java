package booking_bot.models;

import java.time.LocalDateTime;

public class Slot {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private User client;

    public Slot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
        this.client = null;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }


}
