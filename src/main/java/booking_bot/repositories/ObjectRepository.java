package booking_bot.repositories;

import booking_bot.models.BookObject;
import booking_bot.models.Booking;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

public class ObjectRepository implements Repository<Booking> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<BookObject> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        BookObject bookObject = new BookObject();
        //сеттеры
        return bookObject;
    };

    public ObjectRepository(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Booking> findAll() throws DataAccessException {
//        return jdbcTemplate.query("SELECT * FROM slots;", ROW_MAPPER);
        return null;
    }

    @Override
    public void save(Booking entity) throws DataAccessException {
//        String query = String.format("INSERT into slots (time) VALUES ('%s');",
//                entity.getStart(), entity.getEnd());
//        jdbcTemplate.update(query);
    }

    @Override
    public void update(Booking entity) throws DataAccessException{
//        String query = String.format("UPDATE slots SET client = '%d' WHERE start = '%s';",
//                ((Slot) entity).getClient().getId(), ((Slot) entity).getStart(), ((Slot) entity).getEnd());
//        jdbcTemplate.update(query);
    }

    @Override
    public void delete(Object date) throws DataAccessException {
//        String query = String.format("DELETE FROM slots WHERE time = '%s';", date);
//        jdbcTemplate.update(query);
    }
}
