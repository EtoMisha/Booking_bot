package booking_bot.models;

public class User {
    private final Integer id;
    private final String name;
    private final String email;

    public User(Integer id, String name, String login) {
        this.id = id;
        this.name = name;
        this.email = login;
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
