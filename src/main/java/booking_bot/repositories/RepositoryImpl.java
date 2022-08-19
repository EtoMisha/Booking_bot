package booking_bot.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositoryImpl implements Repository {

    private Map<Class, ConcreteRepository> repositories;

    public RepositoryImpl() {
        this.repositories = new HashMap<>();
    }

    @Override
    public List<Object> findAll(Class repoClass) {
        return repositories.get(repoClass).findAll();
    }

    @Override
    public void save(Object entity, Class repoClass) {
        repositories.get(repoClass).save(entity);
    }

    @Override
    public void update(Object entity, Class repoClass) {
        repositories.get(repoClass).update(entity);
    }

    @Override
    public void delete(Object obj, Class repoClass) {
        repositories.get(repoClass).delete(obj);
    }

    @Override
    public Object findByName(String name, Class repoClass) {
        return repositories.get(repoClass).findByName(name);
    }

    @Override
    public void addRepository(Class classname, ConcreteRepository repository) {
        repositories.put(classname, repository);
    }


}
