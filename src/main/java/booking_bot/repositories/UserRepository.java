package booking_bot.repositories;

import booking_bot.models.BookObject;
import booking_bot.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

public class UserRepository implements Repository<User> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setLogin(resultSet.getString("login"));
        user.setRole(resultSet.getString("role"));
        user.setCampus(resultSet.getString("campus"));

        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM booking_objects;", ROW_MAPPER);
    }

    @Override
    public void save(User entity) throws DataAccessException {
        String query = String.format("INSERT into users (name, role, login, campus) VALUES ('%s');",
                entity.getName(), entity.getRole(), entity.getLogin(), entity.getCampus());
        jdbcTemplate.update(query);
    }

    @Override
    public void update(User entity) throws DataAccessException {
        String query = String.format("UPDATE users SET name = '%s' WHERE id = %d;",
                entity.getName(), entity.getRole(), entity.getLogin(), entity.getCampus());
        jdbcTemplate.update(query);
    }

    @Override
    public void delete(int id) throws DataAccessException {
        String query = String.format("DELETE FROM users WHERE id = %d;", id);
        jdbcTemplate.update(query);
    }
}
