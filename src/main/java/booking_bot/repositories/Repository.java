package booking_bot.repositories;

import org.springframework.dao.DataAccessException;

import java.util.List;

public interface Repository<T> {
    List<Object> findAll(Class<T> repoClass) throws DataAccessException;
    void save(T entity, Class<T> repoClass) throws DataAccessException;
    void update(T entity, Class<T> repoClass) throws DataAccessException;
    void delete(Object obj, Class<T> repoClass) throws DataAccessException;

    T findByName(String name, Class<T> repoClass) throws DataAccessException;

    void addRepository(Class classname, ConcreteRepository repository) throws DataAccessException;

}
