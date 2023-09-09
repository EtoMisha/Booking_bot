package ru.booking.bot.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;
import ru.booking.bot.models.Type;

import java.util.List;

public interface TypeRepository extends CrudRepository<Type, Long> {

    @Nonnull
    List<Type> findAll();
}
