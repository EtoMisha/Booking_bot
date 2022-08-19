package booking_bot.repositories;

import booking_bot.models.Role;
import booking_bot.models.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

public class StatusRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Status> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new Status(resultSet.getInt("id"), resultSet.getString("name"));
    };

    public StatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Status> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM statuses;", ROW_MAPPER);
    }

    public void save(Status entity) throws DataAccessException {
        String query = String.format("INSERT into statuses (name) VALUES ('%s');", entity.getName());
        jdbcTemplate.update(query);
    }

    public void update(Status entity) throws DataAccessException {
        String query = String.format("UPDATE statuses SET name = '%s' WHERE id = %d;", entity.getName(), entity.getId());
        jdbcTemplate.update(query);
    }

    public void delete(Status entity) throws DataAccessException {
        String query = String.format("DELETE FROM statuses WHERE id = %d;", entity.getId());
        jdbcTemplate.update(query);
    }

    public Status findByName(String name) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM statuses WHERE name = '" + name + "';", ROW_MAPPER);
    }
}
