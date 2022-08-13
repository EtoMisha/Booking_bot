package booking_bot.models;

import java.util.Date;

public class Slot {
    private final Date date;
    private Client client;

    public Slot(Date date) {
        this.date = date;
        this.client = null;
    }

    public Date getDate() {
        return date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
