package booking_bot.repositories;

import org.springframework.dao.DataAccessException;

import java.util.List;

public interface ConcreteRepository<T> {

    List<T> findAll() throws DataAccessException;
    void save(T entity) throws DataAccessException;
    void update(T entity) throws DataAccessException;
    void delete(T entity) throws DataAccessException;
    T findByName(String name) throws DataAccessException;
}
