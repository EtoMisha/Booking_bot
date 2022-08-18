package booking_bot.repositories;

import booking_bot.models.Campus;
import booking_bot.models.Role;
import booking_bot.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.ui.Model;

import java.sql.ResultSet;
import java.util.List;

public class CampusRepository implements ConcreteRepository<Campus> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Campus> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new Campus(resultSet.getInt("id"), resultSet.getString("name"));
    };

    public CampusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Campus> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM campuses;", ROW_MAPPER);
    }

    @Override
    public void save(Campus entity) throws DataAccessException {
        String query = String.format("INSERT into campuses (name) VALUES ('%s');", entity.getName());
        jdbcTemplate.update(query);
    }

    @Override
    public void update(Campus entity) throws DataAccessException {
        String query = String.format("UPDATE campuses SET name = '%s' WHERE id = %d;", entity.getName(), entity.getId());
        jdbcTemplate.update(query);
    }

    @Override
    public void delete(Campus entity) throws DataAccessException {
        String query = String.format("DELETE FROM campuses WHERE id = %d;", entity.getId());
        jdbcTemplate.update(query);
    }

    @Override
    public Campus findByName(String name) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM campuses WHERE name = '" + name + "';", ROW_MAPPER);
    }


}
