package booking_bot.repositories;

import booking_bot.models.Slot;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

public class SlotsRepository implements Repository<Slot> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Slot> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new Slot(resultSet.getObject("time", LocalDateTime.class));
    };

    public SlotsRepository(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Slot> findAll() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM slots;", ROW_MAPPER);
    }

    @Override
    public void save(Slot entity) throws DataAccessException {
        String query = String.format("INSERT into slots (time) VALUES ('%s');",
                entity.getDate());
        jdbcTemplate.update(query);
    }

    @Override
    public void update(Slot entity) throws DataAccessException{
        String query = String.format("UPDATE slots SET client = '%d' WHERE time = '%s';",
                ((Slot) entity).getClient().getId(), ((Slot) entity).getDate());
        jdbcTemplate.update(query);
    }

    @Override
    public void delete(Object date) throws DataAccessException {
        String query = String.format("DELETE FROM slots WHERE time = '%s';", date);
        jdbcTemplate.update(query);
    }
}
