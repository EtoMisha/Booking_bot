package booking_bot.models;

import java.time.LocalDateTime;

public class Slot implements Comparable {
    private final LocalDateTime date;
    private Client client;

    public Slot(LocalDateTime date) {
        this.date = date;
        this.client = null;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


    @Override
    public int compareTo(Object o) {
        Slot slot = (Slot) o;
        return this.date.compareTo(slot.date);
    }
}
