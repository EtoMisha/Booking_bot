package booking_bot.repositories;

import booking_bot.models.Campus;
import booking_bot.models.Role;
import booking_bot.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        User user = new User();
        user.setId(resultSet.getInt("users.id"));
        user.setName(resultSet.getString("users.name"));
        user.setRole(new Role(resultSet.getInt("roles.id"), resultSet.getString("roles.name")));
        user.setLogin(resultSet.getString("login"));
        user.setCampus(new Campus(resultSet.getInt("campuses.id"), resultSet.getString("campuses.name")));
        user.setTelegramId(resultSet.getLong("telegram_id"));

        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM users, roles, campuses WHERE role_id = roles.id AND campus_id = campuses.id;", ROW_MAPPER);
    }

    public void save(User entity) throws DataAccessException {
        String query = String.format("INSERT into users (name, role_id, login, telegram_id, campus_id) VALUES ('%s', %d, '%s', %d, %d);",
                entity.getName(), entity.getRole().getId(), entity.getLogin(), entity.getTelegramId(), entity.getCampus().getId());
        jdbcTemplate.update(query);
    }

    public void update(User entity) throws DataAccessException {
        String query = String.format("UPDATE users SET name = '%s', role_id = %d, login = '%s', campus_id = %d WHERE id = %d;",
                entity.getName(), entity.getRole().getId(), entity.getLogin(), entity.getCampus().getId(), entity.getId());
        jdbcTemplate.update(query);
    }

    public void delete(User entity) throws DataAccessException {
        String query = String.format("DELETE FROM users WHERE id = %d;", entity.getId());
        jdbcTemplate.update(query);
    }

    public User findByName(String name) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM users, roles, campuses WHERE role_id = roles.id AND campus_id = campuses.id AND users.name = '" + name + "';", ROW_MAPPER);
    }

    public User findByTelegram(Long id) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM users, roles, campuses WHERE role_id = roles.id AND campus_id = campuses.id AND telegram_id = " + id + ";", ROW_MAPPER);
    }


}
