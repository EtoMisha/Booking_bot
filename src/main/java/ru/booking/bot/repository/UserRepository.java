package ru.booking.bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.booking.bot.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
