package booking_bot.repositories;

import booking_bot.models.Campus;
import booking_bot.models.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

public class RolesRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Role> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new Role(resultSet.getInt("id"), resultSet.getString("name"));
    };

    public RolesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Role> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM roles;", ROW_MAPPER);
    }

    public void save(Role entity) throws DataAccessException {
        String query = String.format("INSERT into roles (name) VALUES ('%s');", entity.getName());
        jdbcTemplate.update(query);
    }

    public void update(Role entity) throws DataAccessException {
        String query = String.format("UPDATE roles SET name = '%s' WHERE id = %d;", entity.getName(), entity.getId());
        jdbcTemplate.update(query);
    }

    public void delete(Role entity) throws DataAccessException {
        String query = String.format("DELETE FROM roles WHERE id = %d;", entity.getId());
        jdbcTemplate.update(query);
    }

    public Role findByName(String name) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM roles WHERE name = '" + name + "';", ROW_MAPPER);
    }
}
