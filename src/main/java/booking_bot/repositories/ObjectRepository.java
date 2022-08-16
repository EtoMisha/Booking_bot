package booking_bot.repositories;

import booking_bot.models.BookObject;
import booking_bot.models.Booking;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

public class ObjectRepository implements Repository<BookObject> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<BookObject> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        BookObject bookObject = new BookObject();
        bookObject.setId(resultSet.getInt("id"));
        bookObject.setCategory(resultSet.getString("category"));
        bookObject.setName(resultSet.getString("name"));
        bookObject.setDescription(resultSet.getString("description"));
        bookObject.setImage(resultSet.getString("image"));
        bookObject.setCampus(resultSet.getString("campus"));

        return bookObject;
    };

    public ObjectRepository(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<BookObject> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM booking_objects;", ROW_MAPPER);
    }

    @Override
    public void save(BookObject entity) throws DataAccessException {
        String query = String.format("INSERT into booking_objects (type_id) VALUES ('%s');",
                entity.getId(), entity.getCategory(), entity.getName(),
                entity.getDescription(), entity.getImage(), entity.getCampus());
        jdbcTemplate.update(query);
    }

    @Override
    public void update(BookObject entity) throws DataAccessException {
        String query = String.format("UPDATE booking_objects SET type_id = '%d' WHERE id = %d;",
                entity.getCategory(), entity.getName(), entity.getDescription(),
                entity.getImage(), entity.getCampus(), entity.getId());
        jdbcTemplate.update(query);
    }

    @Override
    public void delete(int id) throws DataAccessException {
        String query = String.format("DELETE FROM booking_objects WHERE id = %d;", id);
        jdbcTemplate.update(query);
    }
}
