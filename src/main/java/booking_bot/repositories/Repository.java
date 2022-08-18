package booking_bot.repositories;

import java.util.List;

public interface Repository<T> {
    List<Object> findAll(Class<T> repoClass);
    void save(T entity, Class<T> repoClass);
    void update(T entity, Class<T> repoClass);
    void delete(Object obj, Class<T> repoClass);

    void addRepository(Class classname, ConcreteRepository repository);

}
