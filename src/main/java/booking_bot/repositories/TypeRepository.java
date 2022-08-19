package booking_bot.repositories;

import booking_bot.models.Status;
import booking_bot.models.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

public class TypeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Type> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new Type(resultSet.getInt("id"), resultSet.getString("name"));
    };

    public TypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Type> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM types;", ROW_MAPPER);
    }

    public void save(Type entity) throws DataAccessException {
        String query = String.format("INSERT into types (name) VALUES ('%s');", entity.getName());
        jdbcTemplate.update(query);
    }

    public void update(Type entity) throws DataAccessException {
        String query = String.format("UPDATE types SET name = '%s' WHERE id = %d;", entity.getName(), entity.getId());
        jdbcTemplate.update(query);
    }

    public void delete(Type entity) throws DataAccessException {
        String query = String.format("DELETE FROM types WHERE id = %d;", entity.getId());
        jdbcTemplate.update(query);
    }

    public Type findByName(String name) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM types WHERE name = '" + name + "';", ROW_MAPPER);
    }
}
