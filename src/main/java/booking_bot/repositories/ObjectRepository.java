package booking_bot.repositories;

import booking_bot.models.BookObject;
import booking_bot.models.Campus;
import booking_bot.models.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

public class ObjectRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<BookObject> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        BookObject bookObject = new BookObject();
        bookObject.setId(resultSet.getInt("booking_objects.id"));
        bookObject.setType(new Type(resultSet.getInt("types.id"), resultSet.getString("types.name")));
        bookObject.setName(resultSet.getString("booking_objects.name"));
        bookObject.setDescription(resultSet.getString("description"));
        bookObject.setImage(resultSet.getString("image"));
        bookObject.setCampus(new Campus(resultSet.getInt("campuses.id"), resultSet.getString("campuses.name")));

        return bookObject;
    };

    public ObjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<BookObject> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM booking_objects, types, campuses WHERE type_id = types.id AND campus_id = campuses.id;", ROW_MAPPER);
    }

    public void save(BookObject entity) throws DataAccessException {
        String query = String.format("INSERT into booking_objects (type_id, name, description, image, campus_id, floor, room_number)" +
                        "VALUES (%d, '%s', '%s', '%s', %d, %d, %d);",
                entity.getType().getId(), entity.getName(), entity.getDescription(),
                entity.getImage(), entity.getCampus().getId(), entity.getFloor(), entity.getRoom_number());
        jdbcTemplate.update(query);
    }

    public void update(BookObject entity) throws DataAccessException {
        String query = String.format("UPDATE booking_objects SET type_id = %d, name = '%s', description = '%s', " +
                        "image = '%s', campus_id = %d, floor = %d, room_number = %d WHERE id = %d;",
                entity.getType().getId(), entity.getName(), entity.getDescription(), entity.getImage(),
                entity.getCampus().getId(), entity.getFloor(), entity.getRoom_number(), entity.getId());
        System.out.println("OBJ REPO: \n" + query);
        jdbcTemplate.update(query);
    }

    public void delete(BookObject entity) throws DataAccessException {
        String query = String.format("DELETE FROM booking_objects WHERE id = %d;", entity.getId());
        jdbcTemplate.update(query);
    }

    public BookObject findByName(String name) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM booking_objects, types, campuses WHERE type_id = types.id AND campus_id = campuses.id AND booking_objects.name = '" + name + "';", ROW_MAPPER);
    }

    public List<BookObject> findByType(String typeName) throws DataAccessException {
        //TODO поправить запрос чтоб по types.name сверялся
        return jdbcTemplate.query("SELECT * FROM booking_objects, types, campuses WHERE type_id = types.id AND campus_id = campuses.id AND types.name = '" + typeName + "';", ROW_MAPPER);
    }
}
