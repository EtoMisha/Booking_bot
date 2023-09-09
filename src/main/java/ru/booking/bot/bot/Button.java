package ru.booking.bot.bot;

import lombok.Data;

@Data
public class Button {
    String label;
    String callBack;

    public Button(String callBack, String label) {
        this.callBack = callBack;
        this.label = label;
    }
}
