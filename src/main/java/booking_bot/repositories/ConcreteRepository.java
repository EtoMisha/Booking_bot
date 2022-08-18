package booking_bot.repositories;

import org.springframework.dao.DataAccessException;

import java.util.List;

public interface ConcreteRepository<T> {

    List<T> findAll() throws DataAccessException;
    void save(T entity) throws DataAccessException;
    void update(T entity) throws DataAccessException;
    void delete(Object obj) throws DataAccessException;
}
