package ru.booking.bot.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "types")
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
