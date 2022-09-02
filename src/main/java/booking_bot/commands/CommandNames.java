package booking_bot.commands;

public enum CommandNames {

    ADD_OBJECT("Добавить объект"),
    EDIT_OBJECT("Редактировать объект"),
    EDIT_USER("Управление пользователями"),
    MY_BOOKINGS("Мои бронирования"),
    NEW_BOOKING("Забронировать"),
    START("/start"),
    ADMIN("/admin");

    private final String text;

    CommandNames(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
