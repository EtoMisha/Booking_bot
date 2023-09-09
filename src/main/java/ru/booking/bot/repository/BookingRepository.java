package ru.booking.bot.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;
import ru.booking.bot.models.Booking;
import ru.booking.bot.models.BookingObject;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {
    @Nonnull
    List<Booking> findAll();
    List<Booking> findByTimeStartBetweenAndBookingObject(LocalDateTime fromTime, LocalDateTime toTime, BookingObject object);
    List<Booking> findByUserId(long userId);
}
