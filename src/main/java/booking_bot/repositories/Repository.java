package booking_bot.repositories;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(int id);

}
