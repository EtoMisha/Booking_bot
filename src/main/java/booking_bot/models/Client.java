package booking_bot.models;

public class Client {
    private final Integer id;
    private final String name;
    private final String email;

    public Client(Integer id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.email = phone;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
