package ru.booking.bot.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "booking_objects")
public class BookingObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String image;
    private LocalTime availableFrom;
    private LocalTime availableTo;

    @ManyToOne
    private Type type;

    public String getFullText() {
        return name + (description == null ? "" : ("\n" + description));
    }
}
