package booking_bot.Repositories;

import booking_bot.models.Client;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(Object obj);

}
