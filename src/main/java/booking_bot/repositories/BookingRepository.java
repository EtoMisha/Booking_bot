package booking_bot.repositories;

import booking_bot.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

public class BookingRepository implements ConcreteRepository<Booking> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Booking> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        Booking booking = new Booking();
        booking.setId(resultSet.getInt("booking.id"));
        booking.setTimeStart(resultSet.getTimestamp("time_start").toLocalDateTime());
        booking.setTimeEnd(resultSet.getTimestamp("time_end").toLocalDateTime());
        booking.setStatus(new Status(resultSet.getInt("statuses.id"), resultSet.getString("statuses.name")));

        BookObject bookObject = new BookObject();
        bookObject.setId(resultSet.getInt("booking_objects.id"));
        bookObject.setType(new Type(resultSet.getInt("types.id"), resultSet.getString("types.name")));
        bookObject.setName(resultSet.getString("booking_objects.name"));
        bookObject.setDescription(resultSet.getString("description"));
        bookObject.setImage(resultSet.getString("image"));
        bookObject.setCampus(new Campus(resultSet.getInt("campuses.id"), resultSet.getString("campuses.name")));
        booking.setBookObject(bookObject);

        User user = new User();
        user.setId(resultSet.getInt("users.id"));
        user.setName(resultSet.getString("users.name"));
        user.setRole(new Role(resultSet.getInt("roles.id"), resultSet.getString("roles.name")));
        user.setLogin(resultSet.getString("login"));
        user.setCampus(new Campus(resultSet.getInt("campuses.id"), resultSet.getString("campuses.name")));
        booking.setUser(user);

        return booking;
    };

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Booking> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM boоkings, statuses, booking_objects, types, campuses, users, roles " +
                "WHERE status_id = statuses.id AND booking_object_id = booking_objects.id AND type_id = types.id " +
                "AND booking_objects.campus_id = campuses.id AND user_id = users.id AND role_id = roles.id AND users.campus_id = campuses.id", ROW_MAPPER);

    }

    @Override
    public void save(Booking entity) throws DataAccessException {
        String query = String.format("INSERT into bookings (time_start, time_end, status_id, booking_object_id, user_id)" +
                        "VALUES ('%s', '%s', %d, %d, %d);",
                entity.getTimeStart(), entity.getTimeEnd(), entity.getStatus().getId(),
                entity.getBookObject().getId(), entity.getUser().getId());
        jdbcTemplate.update(query);
    }

    @Override
    public void update(Booking entity) throws DataAccessException {
        String query = String.format("UPDATE bookings SET time_start = '%s', time_end = '%s', status_id = %d, " +
                        "booking_object_id = %d, user_id = %d WHERE id = %d;",
                entity.getTimeStart(), entity.getTimeEnd(), entity.getStatus().getId(),
                entity.getBookObject().getId(), entity.getUser().getId(), entity.getId());
        jdbcTemplate.update(query);
    }

    @Override
    public void delete(Booking entity) throws DataAccessException {
        String query = String.format("DELETE FROM bookings WHERE id = %d;", entity.getId());
        jdbcTemplate.update(query);
    }

    @Override
    public Booking findByName(String name) throws DataAccessException {
        return null;
    }
}
