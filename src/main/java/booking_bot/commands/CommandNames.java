package booking_bot.commands;

public enum CommandNames {

    NEW_SLOT("Новый слот"),
    REMOVE_SLOT("Удалить слот"),
    SHOW_SLOTS("Посмотреть слоты"),
    SETUP_SLOTS("Настроить слоты"),
    SETUP_NOTIFICATIONS("Настроить уведомления"),
    START("/start"),
    UNKNOWN("Неизвестная команда");

    private final String text;

    CommandNames(String s) {
        text = s;
    }

    public String getText() {
        return text;
    }
}
